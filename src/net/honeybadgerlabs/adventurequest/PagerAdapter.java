package net.honeybadgerlabs.adventurequest;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
  private final static String TAG = "PagerAdapter";

  private Context mContext;
  private List<Fragment> mFragments;

  public PagerAdapter(Context context, FragmentManager manager, List<Fragment> fragments) {
    super(manager);

    mContext = context;
    mFragments = fragments;
  }

  @Override public Fragment getItem(int position) {
    return mFragments.get(position);
  }

  @Override public int getCount() {
    return mFragments.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return mContext.getString(R.string.tab_current);
      case 1:
        return mContext.getString(R.string.tab_profile);
      case 2:
        return mContext.getString(R.string.tab_history);
      default:
        return "";
    }
  }
}

