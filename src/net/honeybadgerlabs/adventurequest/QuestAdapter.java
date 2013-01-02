package net.honeybadgerlabs.adventurequest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class QuestAdapter extends ArrayAdapter<Quest> {
  private final static String FILE = "archives.dat";

  public QuestAdapter(Context context) {
    super(context, android.R.id.empty);
  }

  @Override public View getView(int pos, View view, ViewGroup group) {
    if (view == null) {
      view = View.inflate(getContext(), R.layout.quest, null);
    }

    Quest quest = getItem(pos);

    ((TextView) view.findViewById(R.id.quest_description)).setText(quest.description);

    int icon = 0;
    switch (quest.status) {
      case Quest.STATUS_ABANDON:
        icon = R.drawable.ic_abandon;
        break;
      case Quest.STATUS_FAILED:
        icon = R.drawable.ic_failure;
        break;
      case Quest.STATUS_COMPLETE:
        icon = R.drawable.ic_success;
        break;
    }

    ((ImageView) view.findViewById(R.id.quest_status)).setImageResource(icon);

    return view;
  }

  public void load() {
    try {
      FileInputStream file = getContext().openFileInput(FILE);
      BufferedInputStream buf = new BufferedInputStream(file);
      DataInputStream in = new DataInputStream(buf);

      while (in.available() > 0) {
        int status = in.readInt();
        String description = in.readUTF();

        add(new Quest(description, status));
      }

      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save() {
    try {
      FileOutputStream file = getContext().openFileOutput(FILE, Context.MODE_PRIVATE);
      BufferedOutputStream buf = new BufferedOutputStream(file);
      DataOutputStream out = new DataOutputStream(buf);

      for (int i = 0; i < getCount(); i++) {
        Quest q = getItem(i);
        out.writeInt(q.status);
        out.writeUTF(q.description);
      }

      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
