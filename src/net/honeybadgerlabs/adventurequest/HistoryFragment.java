package net.honeybadgerlabs.adventurequest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryFragment extends ListFragment {
  private QuestAdapter adapter;

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    adapter = ((TitleActivity) getActivity()).getArchiveAdapter();
    setListAdapter(adapter);
  }
}
