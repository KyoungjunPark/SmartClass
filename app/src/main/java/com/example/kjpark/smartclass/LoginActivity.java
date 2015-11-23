/*
 * Copyright (c) 2015. Kyoungjun Park. All Rights Reserved.
 */

package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kjpark.smartclass.utils.ConnectServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    TextView emailTextView;
    TextView passwordTextView;
    TextView newAccountButton;

    EditText emailEditText;
    EditText passwordEditText;

    Button loginButton;

    public static final int REQUEST_CODE_MAIN = 1001;
    public static final int REQUEST_CODE_JOIN = 1002;
    public static final int LOGIN_PERMITTED = 200;
    public static final int LOGIN_DENIED = 404;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextView = (TextView)findViewById(R.id.emailTextView);
        passwordTextView = (TextView)findViewById(R.id.passwordTextView);
        newAccountButton = (Button)findViewById(R.id.newAccountButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //fonts setting
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NanumPen.ttf");
        emailTextView.setTypeface(typeface);
        passwordTextView.setTypeface(typeface);
        newAccountButton.setTypeface(typeface);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivityForResult(intent, REQUEST_CODE_MAIN);
                //finish();

                ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
                    private Boolean isLoginPermitted = false;
                    private String requestMessage;

                    @Override
                    protected Boolean doInBackground(String... params) {
                        URL obj = null;
                        try {
                            obj = new URL("http://165.194.104.22:5000/login");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            //implement below code if token is send to server
                            con = ConnectServer.getInstance().setHeader(con);

                            con.setDoOutput(true);

                            String parameter = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                            parameter += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(parameter);
                            wr.flush();

                            BufferedReader rd = null;

                            if (con.getResponseCode() == LOGIN_PERMITTED) {
                                // 로그인 성공
                                rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                                String token = rd.readLine();
                                ConnectServer.getInstance().setToken(token);

                                isLoginPermitted = true;
                                //userSerialNumber = "RA-SP-BERRY-VERY-GOOD";
                                Log.d("---- success ----", token);


                            } else {
                                // 로그인 실패
                                rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                                isLoginPermitted = false;
                                requestMessage = rd.readLine();
                                Log.d("---- failed ----", String.valueOf(rd.readLine()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (isLoginPermitted) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_MAIN);
                            finish();
                        } else {
                            AlertDialog dialog = createDialogBox(requestMessage);
                            dialog.show();
                            emailTextView.setText("");
                            passwordTextView.setText("");
                        }

                    }
                });
                ConnectServer.getInstance().execute();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
    public void onNewAccountButtonClicked(View v){
        Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
        startActivity(intent);
    }
    public void onLoginButtonClicked(View v){
        /*
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private Boolean isLoginPermitted;
            private String requestMessage;

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/login");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    //implement below code if token is send to server
                    con = ConnectServer.getInstance().setHeader(con);

                    con.setDoOutput(true);

                    String parameter = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    parameter += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(parameter);
                    wr.flush();

                    BufferedReader rd = null;

                    if (con.getResponseCode() == LOGIN_PERMITTED) {
                        // 로그인 성공

                        rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        String token = rd.readLine();
                        ConnectServer.getInstance().setToken(token);


                        isLoginPermitted = true;
                        //userSerialNumber = "RA-SP-BERRY-VERY-GOOD";
                        Log.d("---- success ----", token);


                    } else {
                        // 로그인 실패
                        rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                        isLoginPermitted = false;
                        requestMessage = rd.readLine();
                        Log.d("---- failed ----", String.valueOf(rd.readLine()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (isLoginPermitted) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MAIN);
                    finish();
                } else {
                    AlertDialog dialog = createDialogBox(requestMessage);
                    dialog.show();
                    emailTextView.setText("");
                    passwordTextView.setText("");
                }

            }
        });
        ConnectServer.getInstance().execute();
*/
    }
    private AlertDialog createDialogBox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("로그인 실패");

        builder.setMessage("아이디와 비밀번호를 확인해주세요. \n" + msg + "\n");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog dialog = builder.create();
        return dialog;

    }
}
