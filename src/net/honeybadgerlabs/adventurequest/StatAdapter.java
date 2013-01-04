package net.honeybadgerlabs.adventurequest;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;

public class StatAdapter extends ArrayAdapter<Stat> {
  private final static String TAG = "StatAdapter";
  private final static String FILE = "attributes.dat";
  private final static int MAGIC = 0x0a55;

  private int sum = 0;
  private Comparator comp;

  public StatAdapter(Context context) {
    super(context, android.R.id.empty);

    comp = new Comparator<Stat>() {
      public int compare(Stat a, Stat b) {
        return a.name.compareTo(b.name);
      }
    };
  }

  @Override public View getView(int pos, View view, ViewGroup group) {
    if (view == null) {
      view = View.inflate(getContext(), R.layout.stat, null);
    }

    Stat stat = getItem(pos);
    ((TextView) view.findViewById(R.id.stat)).setText(String.format("%+d %s", stat.value, stat.name));

    return view;
  }

  public void addStat(String name) {
    int count = getCount();

    sum++;

    for (int i = 0; i < count; ++i) {
      Stat s = getItem(i);
      if (s.name.equals(name)) {
        s.value++;
        notifyDataSetChanged();
        return;
      }
    }

    add(new Stat(name, 1));
    sort(comp);
  }

  public int totalPoints() {
    return sum;
  }

  public void load() {
    try {
      FileInputStream file = getContext().openFileInput(FILE);
      BufferedInputStream buf = new BufferedInputStream(file);
      DataInputStream in = new DataInputStream(buf);

      int magic = in.readInt();

      if (magic == MAGIC) {
        int version = in.readInt();
        Log.d(TAG, "Reading attributes version " + version);

        switch (version) {
          case 1:
            loadVersion1(in);
            break;
          default:
            Log.d(TAG, "Uknown attributes version");
            break;
        }
      } else {
        Log.d(TAG, "Invalid attribute data");
      }

      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadVersion1(DataInputStream in) throws IOException {
    int count = in.readInt();
    for (int i = 0; i < count; ++i) {
      String name = in.readUTF();
      int value = in.readInt();

      add(new Stat(name, value));
      sum += value;
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
        Stat s = getItem(i);
        out.writeUTF(s.name);
        out.writeInt(s.value);
      }

      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
