package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kjpark.smartclass.utils.ConnectServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by parkk on 2015-11-20.
 */
public class JoinParentActivity extends AppCompatActivity {

    private final static String TAG = "JoinParentActivity";
    private Toolbar toolbar;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText codeEditText;

    private ImageButton manImageButton;
    private ImageButton womanImageButton;
    private Button createButton;

    private boolean isManClicked = false;
    private boolean isWomanClicked = false;

    public static final int JOIN_PERMITTED = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_parent);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.joinParent);
        setToolbar();

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        codeEditText = (EditText) findViewById(R.id.codeEditText);

        manImageButton = (ImageButton) findViewById(R.id.manImageButton);
        womanImageButton = (ImageButton) findViewById(R.id.womanImageButton);
        createButton = (Button) findViewById(R.id.createButton);

        createButton.setEnabled(false);
        createButton.setBackgroundColor(Color.GRAY);

        nameEditText.addTextChangedListener(filledWatcher);
        emailEditText.addTextChangedListener(filledWatcher);
        passwordEditText.addTextChangedListener(filledWatcher);
        codeEditText.addTextChangedListener(filledWatcher);

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
    public void onManImageButtonClicked(View v){
        if(isManClicked){
            manImageButton.setImageResource(R.drawable.man_before);
            isManClicked = false;
        }else{
            manImageButton.setImageResource(R.drawable.man_after);
            if(isWomanClicked){
                womanImageButton.setImageResource(R.drawable.woman_before);
                isWomanClicked= false;
            }
            isManClicked = true;
        }
    }
    public void onWomanImageButtonClicked(View v){
        if(isWomanClicked){
            womanImageButton.setImageResource(R.drawable.woman_before);
            isWomanClicked = false;
        }else{
            womanImageButton.setImageResource(R.drawable.woman_after);
            if(isManClicked){
                manImageButton.setImageResource(R.drawable.man_before);
                isManClicked= false;
            }
            isWomanClicked = true;
        }
    }
    public void onCreateButtonClicked(View v)
    {
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        final String name = nameEditText.getText().toString();
        final String sex_type = (isManClicked ? "1" : "0");
        final String code = codeEditText.getText().toString();

        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private String requestMessage;

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/join_parent");

                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
                    con.setDoOutput(true);

                    String parameter = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    parameter += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    parameter += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    parameter += "&" + URLEncoder.encode("sex_type", "UTF-8") + "=" + URLEncoder.encode(sex_type, "UTF-8");
                    parameter += "&" + URLEncoder.encode("code", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8");

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(parameter);
                    wr.flush();
                    BufferedReader rd = null;

                    if (con.getResponseCode() == JOIN_PERMITTED) {
                        // 회원가입 성공
                        requestMessage = JOIN_PERMITTED + "";
                    } else {
                        // 회원가입 실패
                        rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));

                        requestMessage = rd.readLine();
                        Log.d("----- server -----", String.valueOf(rd.readLine()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                AlertDialog dialog = createDialogBox(requestMessage);
                dialog.show();
            }
        });
        ConnectServer.getInstance().execute();
    }

    private AlertDialog createDialogBox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (msg == JOIN_PERMITTED + "") {
            builder.setTitle("회원가입 성공");

            builder.setMessage("환영합니다! \n서비스를 이용하려면 로그인해주세요. \n\n");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    // 플래그 변경했음.
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                }
            });
        } else {
            builder.setTitle("회원가입 실패");

            // 에러 메시지 전송
            builder.setMessage(msg + "\n");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        }

        AlertDialog dialog = builder.create();
        return dialog;
    }
    TextWatcher filledWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(nameEditText.getText().toString().equals("")
                    || passwordEditText.getText().toString().equals("")
                    || emailEditText.getText().toString().equals("")
                    || codeEditText.getText().toString().equals("")
                    || (!isManClicked && !isWomanClicked)){
                createButton.setEnabled(false);
                createButton.setBackgroundColor(Color.GRAY);
            } else{
                createButton.setEnabled(true);
                createButton.setBackgroundColor(Color.parseColor("#00aaff"));
            }
        }
    };

}