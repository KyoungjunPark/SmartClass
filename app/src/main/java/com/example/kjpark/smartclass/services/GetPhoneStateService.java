package com.example.kjpark.smartclass.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kjpark.smartclass.MainActivity;
import com.example.kjpark.smartclass.R;
import com.example.kjpark.smartclass.data.NoticeListData;
import com.example.kjpark.smartclass.data.PhoneStateData;
import com.example.kjpark.smartclass.utils.ConnectServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by parkk on 2015-12-01.
 */
public class GetPhoneStateService extends Service {

    private static final String TAG = "GetPhoneStateService";
    private boolean isEnd = false;
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        final Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Running");
                new DoBackgroundTask().execute();
                if(isEnd) {
                    t.cancel();
                    stopSelf();
                }
            }
        }, 0, 10000);
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
        isEnd = true;
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
        private List<PhoneStateData> result = new ArrayList<PhoneStateData>();

        @Override
        protected Long doInBackground(URL... params) {
            URL obj = null;
            try {
                obj = new URL("http://165.194.104.22:5000/screen_status");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //implement below code if token is send to server
                con = ConnectServer.getInstance().setHeader(con);
                con.setDoOutput(true);

                //String parameter = URLEncoder.encode("screen_status", "UTF-8") + "=" + URLEncoder.encode((isOn ? "on" : "off"), "UTF-8");

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                //wr.write(parameter);
                wr.flush();

                BufferedReader rd = null;
                if (con.getResponseCode() == 200) {
                    rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String tmpString = rd.readLine();

                    JSONArray jsonArray = new JSONArray(tmpString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        String email = (String) object.get("email");
                        String status = (String) object.get("status");

                        result.add(new PhoneStateData(email, status));
                    }

                    Log.d(TAG,"---- success ----");
                } else {
                    // 실패
                    rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                    Log.d(TAG, "---- failed ----");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            for(int i = 0 ; i < result.size(); i++) {
                Intent intent = new Intent(getApplication(), GetPhoneStateService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.abc_btn_check_material)
                        .setContentTitle(result.get(i).status)
                        .setContentText(result.get(i).email)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

    }

}
