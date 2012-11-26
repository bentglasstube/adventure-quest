package org.eatabrick.adventurequest;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class TitleActivity extends Activity {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.title);

    findViewById(R.id.stat_experience).setEnabled(false);
    ((TextView) findViewById(R.id.quest_description)).setMovementMethod(new ScrollingMovementMethod());
  }
}
