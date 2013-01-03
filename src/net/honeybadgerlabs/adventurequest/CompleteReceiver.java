package net.honeybadgerlabs.adventurequest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CompleteReceiver extends BroadcastReceiver {
  private static final String TAG = "CompleteReceiver";
  public static final int ID_NOTIFICATION = 0;

  @Override public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "Got completion event with action " + intent.getAction());

    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

    if (settings.getBoolean("notify", true)) {
      PendingIntent showActivityIntent = PendingIntent.getActivity(context, 0, new Intent(context, TitleActivity.class), 0);
      String message;
      Uri sound;

      if (intent.getAction().equals(TitleActivity.ACTION_SUCCESS)) {
        sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.success);
        message = context.getString(R.string.notification_success);
      } else {
        sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.failure);
        message = context.getString(R.string.notification_failure);
      }

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

      builder.setSmallIcon(R.drawable.ic_notify);
      builder.setTicker(message);
      builder.setContentIntent(showActivityIntent);
      builder.setWhen(0);
      builder.setContentTitle(context.getString(R.string.app_name));
      builder.setContentText(message);
      builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
      builder.setSound(sound);
      builder.setAutoCancel(true);

      ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(ID_NOTIFICATION, builder.build());
    }
  }
}
