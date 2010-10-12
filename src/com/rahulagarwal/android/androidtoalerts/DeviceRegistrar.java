package com.rahulagarwal.android.androidtoalerts;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import 	org.apache.http.client.methods.HttpPost;
import 	org.apache.http.impl.client.DefaultHttpClient;
import 	org.apache.http.client.entity.UrlEncodedFormEntity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;


public class DeviceRegistrar {
    

    public static void registerWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() {
            public void run() {
                Intent updateUIIntent = new Intent(Constants.UPDATE_UI_ACTION);
                try {
                    HttpResponse res = makeRequest(context, deviceRegistrationID, Constants.REGISTER_PATH);
                    if (res.getStatusLine().getStatusCode() == 200) {
                        SharedPreferences settings = context.getSharedPreferences(Constants.PREF_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("pushRegistrationID", deviceRegistrationID);
                        editor.commit();
                        updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.REGISTERED_STATUS);
                    } else if (res.getStatusLine().getStatusCode() == 400) {
                        updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.AUTH_ERROR_STATUS);
                    } else {
                        Log.w(Constants.LOG_TAG, "Registration error " +
                                String.valueOf(res.getStatusLine().getStatusCode()));
                        updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.ERROR_STATUS);
                    }
                    context.sendBroadcast(updateUIIntent);
                } catch (Exception e) {
                    Log.w(Constants.LOG_TAG, "Registration error " + e.getMessage());
                    updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.ERROR_STATUS);
                    context.sendBroadcast(updateUIIntent);
                }
            }
        }).start();
    }

    public static void unregisterWithServer(final Context context, final String deviceRegistrationID) {
        new Thread(new Runnable() {
            public void run() {
                Intent updateUIIntent = new Intent(Constants.UPDATE_UI_ACTION);
                try {
                    HttpResponse res = makeRequest(context, deviceRegistrationID, Constants.UNREGISTER_PATH);
                    if (res.getStatusLine().getStatusCode() == 200) {
                        SharedPreferences settings = context.getSharedPreferences(Constants.PREF_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("deviceRegistrationID");
                        editor.commit();
                        updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.UNREGISTERED_STATUS);
                    } else {
                        Log.w(Constants.LOG_TAG, "Unregistration error " +
                                String.valueOf(res.getStatusLine().getStatusCode()));
                        updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.ERROR_STATUS);
                    }
                } catch (Exception e) {
                    updateUIIntent.putExtra(Constants.STATUS_EXTRA, Constants.ERROR_STATUS);
                    Log.w(Constants.LOG_TAG, "Unegistration error " + e.getMessage());
                }
                context.sendBroadcast(updateUIIntent);
            }
        }).start();
    }

    private static HttpResponse makeRequest(Context context, String deviceRegistrationID, String urlPath) throws Exception {
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("registration_id", deviceRegistrationID));

        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (deviceId != null) {
            urlPath += deviceId + "/";
        }
        
        DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(urlPath);
		httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		Log.d(Constants.LOG_TAG, "Registration URL: " + urlPath);
		return httpclient.execute(httpost);
    }
}
