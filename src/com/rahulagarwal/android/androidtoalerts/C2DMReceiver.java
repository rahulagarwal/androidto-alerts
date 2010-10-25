package com.rahulagarwal.android.androidtoalerts;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMReceiver extends C2DMBaseReceiver {
	
	public C2DMReceiver() {
        super(Constants.SENDER_ACCOUNT_EMAIL);
    }

    @Override
    public void onRegistrered(Context context, String registration) {
        DeviceRegistrar.registerWithServer(context, registration);
    }

    @Override
    public void onUnregistered(Context context) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, 0);
        String deviceRegistrationID = prefs.getString("pushRegistrationID", null);
        DeviceRegistrar.unregisterWithServer(context, deviceRegistrationID);
    }

    @Override
    public void onError(Context context, String errorId) {
    	context.sendBroadcast(new Intent(Constants.UPDATE_UI_ACTION_ERROR));
    }

    @Override
    public void onMessage(Context context, Intent intent) {
       Log.d(Constants.LOG_TAG, "NEW C2DM Message Received");
       Bundle extras = intent.getExtras();
       if (extras != null) {
           String message = (String) extras.get("alert");
           Log.d(Constants.LOG_TAG, "NEW C2DM Alert! - " + message);
           if (message != null) {
        	   setLastAlert(context, message);
        	   Intent launchIntent = new Intent(context, MainActivity.class);
        	   generateNotification(context, message, launchIntent);
           }
       }
   }

    public void setLastAlert(Context context, String lastAlert) {
        final SharedPreferences settings = getSharedPreferences(Constants.PREF_NAME, 0);
        Editor editor = settings.edit();
        editor.putString(Constants.LAST_ALERT_MESSAGE, lastAlert);
        editor.commit();

    }

    private void generateNotification(Context context, String title, Intent intent) {
       int icon = R.drawable.status_icon;
       long when = System.currentTimeMillis();

       Notification notification = new Notification(icon, title, when);
       notification.setLatestEventInfo(context, getString(R.string.new_alert), title,
               PendingIntent.getActivity(context, 0, intent, 0));
       notification.flags |= Notification.FLAG_AUTO_CANCEL;

       SharedPreferences settings = getSharedPreferences(Constants.PREF_NAME, 0);
       int notificatonID = settings.getInt("notificationID", 0);

       NotificationManager nm =
               (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
       nm.notify(notificatonID, notification);
       playNotificationSound(context);

       SharedPreferences.Editor editor = settings.edit();
       editor.putInt("notificationID", ++notificatonID % 32);
       editor.commit();
   }

   private void playNotificationSound(Context context) {
       Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       if (uri != null) {
           Ringtone rt = RingtoneManager.getRingtone(context, uri);
           if (rt != null) {
               rt.setStreamType(AudioManager.STREAM_NOTIFICATION);
               rt.play();
           }
       }
   }

}
