package net.honeybadgerlabs.adventurequest;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CurrentFragment extends Fragment implements TitleActivity.UpdateListener {
  private TextView textDescription;
  private TextView textStatus;
  private Button   buttonAction;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.current, container, false);

    textDescription = (TextView) view.findViewById(R.id.quest_description);
    textStatus      = (TextView) view.findViewById(R.id.quest_status);
    buttonAction    = (Button) view.findViewById(R.id.quest_action);

    textDescription.setMovementMethod(new ScrollingMovementMethod());

    return view;
  }

  @Override public void onResume() {
    super.onResume();
    ((TitleActivity) getActivity()).addUpdateListener(this);
  }

  @Override public void onPause() {
    super.onPause();
    ((TitleActivity) getActivity()).removeUpdateListener(this);
  }

  public void onUpdate(String description, int status, long end) {
    textDescription.setText(description);

    switch (status) {
      case Quest.STATUS_NONE:
        textStatus.setText(R.string.status_none);
        buttonAction.setText(R.string.action_new);
        break;
      case Quest.STATUS_FAILED:
        textStatus.setText(R.string.status_failed);
        buttonAction.setText(R.string.action_new);
        break;
      case Quest.STATUS_COMPLETE:
        textStatus.setText(R.string.status_complete);
        buttonAction.setText(R.string.action_new);
        break;
      case Quest.STATUS_ABANDON:
        textStatus.setText(R.string.status_abandon);
        buttonAction.setText(R.string.action_new);
        break;
      case Quest.STATUS_PROGRESS:
        textStatus.setText(String.format(getString(R.string.status_progress), getQuestETA(end)));
        buttonAction.setText(R.string.action_abandon);
        break;
    }
  }

  private String getQuestETA(long end) {
    long ttl = end - SystemClock.elapsedRealtime();

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

}
