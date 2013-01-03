package net.honeybadgerlabs.adventurequest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelUpDialog extends DialogFragment {
  private static final String TAG = "LevelUpDialog";
  private static final int CHOICES = 5;

  private int mLevel;
  private TextView mMessage;

  public LevelUpDialog(int level) {
    mLevel = level;
  }

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    List<String> attributes = Arrays.asList(getResources().getStringArray(R.array.item_attributes));
    Collections.shuffle(attributes);
    final String[] choices = attributes.subList(0, CHOICES).toArray(new String[CHOICES]);

    Collections.sort(Arrays.asList(choices));

    builder.setTitle(getString(R.string.levelup_message))
           .setItems(choices, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
               Log.d(TAG, "chose " + choices[id]);

               // TODO record attribute choice
             }
           });

    return builder.create();
  }
}
