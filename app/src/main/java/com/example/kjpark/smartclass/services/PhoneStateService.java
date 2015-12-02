package com.example.kjpark.smartclass.services;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kjpark.smartclass.MainActivity;
import com.example.kjpark.smartclass.utils.ConnectServer;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by parkk on 2015-12-01.
 */
public class PhoneStateService extends Service {

    private static final String TAG = "PhoneStateService";
    public boolean isRunning = false;
    private BroadcastReceiver scrOnReceiver;
    private BroadcastReceiver scrOffReceiver;
    private IntentFilter scrOnFilter;
    private IntentFilter scrOffFilter;
    private boolean isOn = true;
    private boolean currentState = true;

    public void onCreate() {
        super.onCreate();
        scrOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SCREEN_ON");
                isOn = true;
            }
        };
        scrOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        scrOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "SCREEN_OFF");
                isOn = false;
            }
        };
        scrOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        registerReceiver(scrOnReceiver, scrOnFilter);
        registerReceiver(scrOffReceiver, scrOffFilter);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        final Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (currentState != isOn) {
                    currentState = isOn;
                    Log.d(TAG, "changed");
                    new DoBackgroundTask().execute();
                }

            }
        }, 0, 5000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public synchronized void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
        @Override
        protected Long doInBackground(URL... params) {
            URL obj = null;
            try {
                obj = new URL("http://165.194.104.22:5000/enroll_screen");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //implement below code if token is send to server
                con = com.example.kjpark.smartclass.utils.ConnectServer.getInstance().setHeader(con);
                con.setDoOutput(true);

                String parameter = URLEncoder.encode("screen_status", "UTF-8") + "=" + URLEncoder.encode((isOn ? "on" : "off"), "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(parameter);
                wr.flush();

                BufferedReader rd = null;
                if (con.getResponseCode() == 200) {
                    // 성공
                    Log.d(TAG,"---- success ----");
                } else {
                    // 실패
                    rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                    Log.d(TAG, "---- failed ----");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
        }

    }

}
