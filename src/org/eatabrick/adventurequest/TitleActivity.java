package org.eatabrick.adventurequest;

import android.app.Activity;
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
  private SharedPreferences settings;

  private int charLevel;
  private int charXP;

  private String questTitle;
  private String questDescription;
  private long questEnd;
  private int questXP;
  private int questStatus;

  static int QUEST_NONE     = 0;
  static int QUEST_FAILED   = 1;
  static int QUEST_COMPLETE = 2;
  static int QUEST_PROGRESS = 3;
  static int QUEST_ABANDON  = 4;

  private Random rng = new Random();
  private CountDownTimer timer;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.title);

    findViewById(R.id.stat_experience).setEnabled(false);
    ((TextView) findViewById(R.id.quest_description)).setMovementMethod(new ScrollingMovementMethod());

    settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
  }

  @Override public void onResume() {
    super.onResume();

    loadGame();
    updateDisplay();
  }

  @Override public void onPause() {
    super.onPause();

    saveGame();
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

    questTitle       = settings.getString("quest_title", getString(R.string.welcome_title));
    questDescription = settings.getString("quest_desc", getString(R.string.welcome_description));
    questStatus      = settings.getInt("quest_status", QUEST_NONE);
    questEnd         = settings.getLong("quest_end", 0);
    questXP          = settings.getInt("quest_xp", 0);

    if (questStatus == QUEST_PROGRESS) startTimer();
  }

  private void saveGame() {
    SharedPreferences.Editor editor = settings.edit();

    editor.putInt("char_level", charLevel);
    editor.putInt("char_xp", charXP);
    editor.putString("quest_title", questTitle);
    editor.putString("quest_desc", questDescription);
    editor.putInt("quest_status", questStatus);
    editor.putLong("quest_end", questEnd);
    editor.putInt("quest_xp", questXP);

    editor.apply();
  }

  private void setText(int id, String text) {
    ((TextView) findViewById(id)).setText(text);
  }

  private void setText(int id, int text) {
    ((TextView) findViewById(id)).setText(text);
  }

  private void updateDisplay() {
    setText(R.id.stat_level, String.format(getString(R.string.stat_level), charLevel));
    setText(R.id.quest_title, questTitle);
    setText(R.id.quest_description, questDescription);

    ((ProgressBar) findViewById(R.id.stat_experience)).setProgress(charXP);

    if (questStatus == QUEST_NONE) {
      setText(R.id.quest_status, "");
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
      return 442212000L + 864000000 * (currentLevel - 60);
    } else {
      return 60000 * (long) (Math.pow(1.16, currentLevel));
    }
  }

  private void startTimer() {
    long time = questEnd - SystemClock.elapsedRealtime();

    if (time > 0) {
      timer = new CountDownTimer(time, 1000) {
        public void onTick(long millisUntilFinished) {
          updateDisplay();
        }
        public void onFinish() {
          completeQuest();
        }
      }.start();
    } else {
      completeQuest();
    }
  }

  private void beginQuest() {
    // pick time and xp
    double factor = rng.nextDouble() / 2.0 + 0.25;
    long time = (long) ((double) timeToLevel(charLevel) * factor);

    questTitle = "Testing";
    questDescription = "This is just a test.";
    questEnd = SystemClock.elapsedRealtime() + time;
    questXP  = rng.nextInt(40) + 20;

    questStatus = QUEST_PROGRESS;
    updateDisplay();

    startTimer();
  }

  private void completeQuest() {
    // 10% chance of failure
    if (rng.nextFloat() > 0.10) {
      charXP += questXP;
      if (charXP > 100) {
        charXP -= 100;
        charLevel += 1;
      }

      questEnd = 0;
      questStatus = QUEST_COMPLETE;
    } else {
      questEnd = 0;
      questStatus = QUEST_FAILED;
    }

    updateDisplay();
  }

  private void abandonQuest() {
    charXP -= questXP / 8;
    if (charXP < 0) charXP = 0;

    questEnd = 0;
    questXP = 0;
    questStatus = QUEST_ABANDON;
    timer.cancel();

    updateDisplay();
  }
}
