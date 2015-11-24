/*
 * Copyright (c) 2015. Kyoungjun Park. All Rights Reserved.
 */

package com.example.kjpark.smartclass;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kjpark.smartclass.utils.ConnectServer;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CodeInfoActivity extends AppCompatActivity {

    private final static String TAG = "CodeInfoActivity";
    private Toolbar toolbar;

    private TextView codeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeinfo);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.codeInfo);
        setToolbar();

        codeTextView = (TextView) findViewById(R.id.codeTextView);
        setCodes();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        menu.removeItem(R.id.action_user);
        menu.removeItem(R.id.action_write);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.left_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void setCodes()
    {
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private String myCode;

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/get_codes");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    //implement below code if token is send to server
                    con = ConnectServer.getInstance().setHeader(con);

                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.flush();

                    BufferedReader rd = null;

                    if (con.getResponseCode() == 200) {
                        rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        String tmpString = rd.readLine();
                        myCode = tmpString;
                        Log.d("---- success ----", tmpString);
                    } else {
                        rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                        Log.d("---- failed ----", String.valueOf(rd.readLine()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                codeTextView.setText(myCode);
            }

        });
        ConnectServer.getInstance().execute();

    }
}
