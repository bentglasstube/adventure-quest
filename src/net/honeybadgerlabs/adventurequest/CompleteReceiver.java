package net.honeybadgerlabs.adventurequest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class CompleteReceiver extends BroadcastReceiver {
  private static final String TAG = "CompleteReceiver";
  public static final int ID_NOTIFICATION = 0;

  @Override public void onReceive(Context context, Intent intent) {
    Notification notification;
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

    Log.d(TAG, "Got completion event with action " + intent.getAction());

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

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notify);
        builder.setTicker(message);
        builder.setContentIntent(showActivityIntent);
        builder.setWhen(0);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText(message);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        builder.setSound(sound);
        builder.setAutoCancel(true);

        notification = builder.getNotification();
      } else {
        notification = new Notification(R.drawable.ic_notify, message, 0);
        notification.setLatestEventInfo(context, context.getString(R.string.app_name), message, showActivityIntent);
        notification.sound = sound;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
      }

      ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).notify(ID_NOTIFICATION, notification);
    }
  }
}
