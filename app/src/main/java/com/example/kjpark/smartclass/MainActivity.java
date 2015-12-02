/*
 * Copyright (c) 2015. Kyoungjun Park. All Rights Reserved.
 */

package com.example.kjpark.smartclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kjpark.smartclass.adapter.ViewPagerAdapter;
import com.example.kjpark.smartclass.services.PhoneStateService;
import com.example.kjpark.smartclass.utils.ConnectServer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"공지사항","과제방","추억 공간", "설정"};
    int NumOfTabs = 4;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String gcm_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumOfTabs);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(viewPager);

        if(ConnectServer.getInstance().getType() == ConnectServer.Type.student) {
            //set phoneState service
            Intent serviceIntent = new Intent(this, PhoneStateService.class);
            startService(serviceIntent);
        }

        registBroadcastReceiver();
        getInstanceIdToken();
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_user){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
        private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
                }else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
                }else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    Log.d(TAG, "token is set in gcm_token");
                    gcm_token = intent.getStringExtra("token");
                    Log.d(TAG, "gcm_token: " + gcm_token);
                    ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {

                        @Override
                        protected Boolean doInBackground(String... params) {
                            URL obj = null;
                            try {
                                obj = new URL("http://165.194.104.22:5000/gcm");
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                //implement below code if token is send to server
                                con = ConnectServer.getInstance().setHeader(con);

                                con.setDoOutput(true);

                                String parameter = URLEncoder.encode("register_id", "UTF-8") + "=" + URLEncoder.encode(gcm_token, "UTF-8");

                                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                                wr.write(parameter);
                                wr.flush();

                                BufferedReader rd = null;

                                if (con.getResponseCode() == 200) {
                                    Log.d(TAG,"gcm_token is sent");
                                } else {
                                    rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                                    Log.e(TAG,rd.readLine());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(Boolean aBoolean) {
                        }
                    });
                    ConnectServer.getInstance().execute();
                }
            }
        };
    }


}
