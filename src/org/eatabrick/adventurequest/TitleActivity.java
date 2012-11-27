package org.eatabrick.adventurequest;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TitleActivity extends Activity {
  private SharedPreferences settings;

  private int charLevel;
  private int charXP;

  private String questTitle;
  private String questDescription;
  private int questEnd;
  private int questXP;
  private int questStatus;

  static int QUEST_NONE     = 0;
  static int QUEST_FAILED   = 1;
  static int QUEST_COMPLETE = 2;
  static int QUEST_PROGRESS = 3;
  static int QUEST_ABANDON  = 4;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.title);

    findViewById(R.id.stat_experience).setEnabled(false);
    ((TextView) findViewById(R.id.quest_description)).setMovementMethod(new ScrollingMovementMethod());

    settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    loadGame();
    updateDisplay();
  }

  private void loadGame() {
    charLevel = settings.getInt("char_level", 1);
    charXP    = settings.getInt("char_xp", 0);

    questTitle       = settings.getString("quest_title", getString(R.string.welcome_title));
    questDescription = settings.getString("quest_desc", getString(R.string.welcome_description));
    questStatus      = settings.getInt("quest_status", QUEST_NONE);
    questEnd         = settings.getInt("quest_end", 0);
    questXP          = settings.getInt("quest_xp", 0);
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
    return "4:42";
  }

  private void generateQuest() {
  }

  private void beginQuest() {
  }

  private void abandonQuest() {
  }
}
