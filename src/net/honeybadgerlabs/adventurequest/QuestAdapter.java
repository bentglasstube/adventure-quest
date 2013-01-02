package net.honeybadgerlabs.adventurequest;

import android.content.Context;
import android.util.Log;
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
import java.util.HashSet;
import java.util.List;

public class QuestAdapter extends ArrayAdapter<Quest> {
  private final static String TAG = "QuestAdapter";
  private final static String FILE = "archives.dat";
  private final static int MAGIC = 0xB00B;

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

      in.mark(8);
      int magic = in.readInt();

      if (magic == MAGIC) {
        int version = in.readInt();
        Log.d(TAG, "Reading archives version " + version);

        switch (version) {
          case 1:
            loadVersion1(in);
            break;
          default:
            Log.d(TAG, "Uknown archive version");
            break;
        }
      } else {
        Log.d(TAG, "Reading legacy archives");

        in.reset();
        loadLegacy(in);
      }

      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadLegacy(DataInputStream in) throws IOException {
    HashSet set = new HashSet<String>(500);
    int dupes = 0;
    int bogus = 0;

    while (in.available() > 0) {
      int status = in.readInt();
      String description = in.readUTF();

      switch (status) {
        case Quest.STATUS_ABANDON:
        case Quest.STATUS_FAILED:
        case Quest.STATUS_COMPLETE:
          if (set.add(description)) {
            add(new Quest(description, status));
          } else {
            dupes++;
          }
          break;
        default:
          bogus++;
      }
    }

    Log.d(TAG, "Skipped " + dupes + " duplicates and " + bogus + " bogus quests");
  }

  private void loadVersion1(DataInputStream in) throws IOException {
    int count = in.readInt();
    for (int i = 0; i < count; ++i) {
      int status = in.readInt();
      String description = in.readUTF();

      add(new Quest(description, status));
    }
  }

  public void save() {
    try {
      FileOutputStream file = getContext().openFileOutput(FILE, Context.MODE_PRIVATE);
      BufferedOutputStream buf = new BufferedOutputStream(file);
      DataOutputStream out = new DataOutputStream(buf);

      out.writeInt(MAGIC);
      out.writeInt(1);

      int count = getCount();

      out.writeInt(count);

      for (int i = 0; i < count; i++) {
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
