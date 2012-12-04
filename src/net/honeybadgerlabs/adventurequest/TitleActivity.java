package net.honeybadgerlabs.adventurequest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.lang.Math;
import java.util.Random;

public class TitleActivity extends Activity {
  private static final int QUEST_NONE     = 0;
  private static final int QUEST_FAILED   = 1;
  private static final int QUEST_COMPLETE = 2;
  private static final int QUEST_PROGRESS = 3;
  private static final int QUEST_ABANDON  = 4;
  private static final int LEVEL_BASE     = 300000;

  public static final String ACTION_SUCCESS = "QuestSuccess";
  public static final String ACTION_FAILURE = "QuestFailure";

  private SharedPreferences settings;
  private Random rng = new Random();
  private CountDownTimer timer;
  private AlarmManager alarm;
  private PendingIntent successIntent;
  private PendingIntent failureIntent;

  private int charLevel;
  private int charXP;
  private String questDescription;
  private long questEnd;
  private long questFailEnd;
  private int questXP;
  private int questStatus;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.title);

    findViewById(R.id.stat_experience).setEnabled(false);
    ((TextView) findViewById(R.id.quest_description)).setMovementMethod(new ScrollingMovementMethod());

    settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
    successIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SUCCESS, null, this, CompleteReceiver.class), 0);
    failureIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_FAILURE, null, this, CompleteReceiver.class), 0);
  }

  @Override public void onResume() {
    super.onResume();

    loadGame();
    updateDisplay();
    setNotify(false);
  }

  @Override public void onPause() {
    super.onPause();

    saveGame();
    if (timer != null) timer.cancel();
    setNotify(true);
  }

  public void setNotify(boolean notify) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putBoolean("notify", notify);
    editor.commit();

    if (notify == false) {
      ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(CompleteReceiver.ID_NOTIFICATION);
    }
  }

  public void onAction(View v) {
    if (questStatus == QUEST_PROGRESS) {
      abandonQuest();
    } else {
      beginQuest();
    }
  }

  private void loadGame() {
    charLevel = settings.getInt("char_level", 1);
    charXP    = settings.getInt("char_xp", 0);

    questDescription = settings.getString("quest_desc", getString(R.string.welcome));
    questStatus      = settings.getInt("quest_status", QUEST_NONE);
    questEnd         = settings.getLong("quest_end", 0);
    questFailEnd     = settings.getLong("quest_fail_end", 0);
    questXP          = settings.getInt("quest_xp", 0);

    if (questStatus == QUEST_PROGRESS) startTimer();
  }

  private void saveGame() {
    SharedPreferences.Editor editor = settings.edit();

    editor.putInt("char_level", charLevel);
    editor.putInt("char_xp", charXP);
    editor.putString("quest_desc", questDescription);
    editor.putInt("quest_status", questStatus);
    editor.putLong("quest_end", questEnd);
    editor.putLong("quest_fail_end", questFailEnd);
    editor.putInt("quest_xp", questXP);

    editor.commit();
  }

  private void setText(int id, String text) {
    ((TextView) findViewById(id)).setText(text);
  }

  private void setText(int id, int text) {
    ((TextView) findViewById(id)).setText(text);
  }

  private void updateDisplay() {
    setText(R.id.stat_level, String.format(getString(R.string.stat_level), charLevel));
    setText(R.id.quest_description, questDescription);

    ((ProgressBar) findViewById(R.id.stat_experience)).setProgress(charXP);

    if (questStatus == QUEST_NONE) {
      setText(R.id.quest_status, R.string.status_none);
      setText(R.id.quest_action, R.string.action_new);
    } else if (questStatus == QUEST_FAILED) {
      setText(R.id.quest_status, R.string.status_failed);
      setText(R.id.quest_action, R.string.action_new);
    } else if (questStatus == QUEST_COMPLETE) {
      setText(R.id.quest_status, R.string.status_complete);
      setText(R.id.quest_action, R.string.action_new);
    } else if (questStatus == QUEST_ABANDON) {
      setText(R.id.quest_status, R.string.status_abandon);
      setText(R.id.quest_action, R.string.action_new);
    } else {
      setText(R.id.quest_status, String.format(getString(R.string.status_progress), getQuestETA()));
      setText(R.id.quest_action, R.string.action_abandon);
    }

    // TODO: remove from release
    ((TextView) findViewById(R.id.quest_status)).setAllCaps(questFailEnd > 0);
  }

  private String getQuestETA() {
    long ttl = questEnd - SystemClock.elapsedRealtime();

    if (ttl < 1000) {
      return "0s";
    } else {
      long days    = ttl / 86400000;
      long hours   = (ttl / 3600000) % 24;
      long minutes = (ttl /   60000) % 60;
      long seconds = (ttl /    1000) % 60;

      if (days > 0) {
        return String.format("%dd%02dh%02dm%02d", days, hours, minutes, seconds);
      } else if (hours > 0) {
        return String.format("%dh%02dm%02ds", hours, minutes, seconds);
      } else if (minutes > 0) {
        return String.format("%dm%02ds", minutes, seconds);
      } else {
        return String.format("%ds", seconds);
      }
    }
  }

  private long timeToLevel(int currentLevel) {
    if (currentLevel > 60) {
      return timeToLevel(60) + 864000000 * (currentLevel - 60);
    } else {
      return LEVEL_BASE * (long) (Math.pow(1.16, currentLevel));
    }
  }

  private void startTimer() {
    long time = (questFailEnd > 0 ? questFailEnd : questEnd) - SystemClock.elapsedRealtime();

    if (time > 0) {
      timer = new CountDownTimer(time, 1000) {
        public void onTick(long millisUntilFinished) {
          updateDisplay();
        }
        public void onFinish() {
          completeQuest();
        }
      }.start();

      // set alarm for notification
      if (questFailEnd > 0) {
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, questFailEnd, failureIntent);
      } else {
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, questEnd, successIntent);
      }
    } else {
      completeQuest();
    }
  }

  private String randomStringFromArray(String name) {
    int id = getResources().getIdentifier(name, "array", getPackageName());

    if (id == 0) {
      return "{" + name + "}";
    } else {
      String[] array = getResources().getStringArray(id);
      return array[rng.nextInt(array.length)];
    }
  }

  private String generateQuestDescription() {
    String quest = randomStringFromArray("quest_base");

    while (quest.indexOf("[") > -1) {
      int start = quest.indexOf("[");
      int end   = quest.indexOf("]");

      String type = quest.substring(start + 1, end);
      String replace = randomStringFromArray("quest_" + type);

      quest = quest.substring(0, start) + replace + quest.substring(end + 1, quest.length());
    }

    return quest;
  }

  private void beginQuest() {
    double factor = rng.nextDouble() / 5.0 + 0.2;
    long time = (long) ((double) timeToLevel(charLevel) * factor);
    long start = SystemClock.elapsedRealtime();

    questDescription = generateQuestDescription();
    questEnd = start + time;
    questXP  = (int) (factor * 100);

    if (rng.nextFloat() < 0.10) {
      double early = rng.nextGaussian() * 0.25 + 0.60;
      if (early > 1.0) early = 0.99;

      questFailEnd = start + (long) ((double) time * early);
    } else {
      questFailEnd = 0;
    }

    questStatus = QUEST_PROGRESS;
    updateDisplay();

    startTimer();
  }

  private void completeQuest() {
    if (questEnd > 0) {
      if (questFailEnd == 0) {
        charXP += questXP;
        if (charXP > 100) {
          charXP -= 100;
          charLevel += 1;
        }

        questStatus = QUEST_COMPLETE;
      } else {
        questStatus = QUEST_FAILED;
      }
    }

    questEnd = 0;
    questFailEnd = 0;
    updateDisplay();
  }

  private void abandonQuest() {
    charXP -= questXP / 8;
    if (charXP < 0) charXP = 0;

    questEnd = 0;
    questFailEnd = 0;
    questXP = 0;
    questStatus = QUEST_ABANDON;
    timer.cancel();

    updateDisplay();
  }
}
