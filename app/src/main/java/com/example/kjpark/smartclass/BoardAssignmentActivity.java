package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kjpark.smartclass.utils.ConnectServer;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by parkk on 2015-11-17.
 */

/*
 * Copyright 2015 Wouter Dullaert. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class BoardAssignmentActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private final String TAG = "BoardAssignmentActivity";
    private Toolbar toolbar;
    private Button startDate;
    private Button startTime;
    private Button endDate;
    private Button endTime;
    private CheckedTextView importanceTextView;

    private EditText titleEditText;
    private EditText contentEditText;

    private Boolean isImportanceCheked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setToolbar();

        startDate = (Button) findViewById(R.id.startDate);
        startTime = (Button) findViewById(R.id.startTime);
        endDate = (Button) findViewById(R.id.endDate);
        endTime = (Button) findViewById(R.id.endTime);
        importanceTextView = (CheckedTextView) findViewById(R.id.importanceTextView);
        importanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImportanceCheked = !isImportanceCheked;
                importanceTextView.setChecked(isImportanceCheked);
            }
        });

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);

        setCurrentTime();
        setOptionMenuSyncChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice_board, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_enroll);

        if(isAnyInputExist()) {
            item.setEnabled(true);
        }else {
            item.setEnabled(false);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_enroll){
            if(isAnyInputExist()){
                Log.d(TAG, startDate.getText().toString() + "::::::" + startTime.getText().toString());
                //send to server the name/ info/ date
                ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
                    private String requestMessage;
                    private int requestCode;
                    private String title = titleEditText.getText().toString();
                    private String content = contentEditText.getText().toString();
                    private String start_date = startDate.getText().toString() +" "+startTime.getText().toString();
                    private String end_date = endDate.getText().toString() +" "+ endTime.getText().toString();
                    private String isImportant = isImportanceCheked.toString();

                    @Override
                    protected Boolean doInBackground(String... params) {
                        URL obj = null;
                        try {
                            obj = new URL("http://165.194.104.22:5000/enroll_assignment");

                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            //implement below code if token is send to server
                            con = ConnectServer.getInstance().setHeader(con);

                            con.setDoOutput(true);

                            String parameter = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                            parameter += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
                            parameter += "&" + URLEncoder.encode("start_date", "UTF-8") + "=" + URLEncoder.encode(start_date, "UTF-8");
                            parameter += "&" + URLEncoder.encode("end_date", "UTF-8") + "=" + URLEncoder.encode(end_date, "UTF-8");
                            parameter += "&" + URLEncoder.encode("isImportant", "UTF-8") + "=" + URLEncoder.encode(isImportant, "UTF-8");

                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(parameter);
                            wr.flush();
                            BufferedReader rd = null;

                            requestCode = con.getResponseCode();
                            if (requestCode == 200) {
                                //enroll success

                            } else {
                                // enroll fail
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
                        AlertDialog dialog = createDialogBox(requestCode, requestMessage);
                        dialog.show();
                    }
                });
                ConnectServer.getInstance().execute();

            }
        }

        return true;
    }
    private AlertDialog createDialogBox(int requestCode, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (requestCode == 200) {
            Toast.makeText(getApplicationContext(), "등록하였습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            intent.putExtra("title", titleEditText.getText().toString());
            finish();
        } else {
            builder.setTitle("등록 실패");

            // 에러 메시지 전송
            builder.setMessage("서버와의 통신이 원활하지 않습니다.\n 다음에 다시 시도해 주세요." + "\n");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        }

        AlertDialog dialog = builder.create();
        return dialog;

    }
    private void setToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_close);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user typing any words.. then handling it!
                if (isAnyInputExist()) {
                    //question that are you really want to go out
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardAssignmentActivity.this);
                    builder.setTitle("변경을 취소하시겠어요?");
                    builder.setMessage("지금 돌아가면 작성 중인 내용이 취소됩니다.");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("유지", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    //go out directly
                    onBackPressed();
                }
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //Log.d(TAG, view.getTag());

        if(view.getTag().equals("StartDatepickerdialog")){
            startDate.setText(year+"/"+monthOfYear+"/"+dayOfMonth);

        }else if(view.getTag().equals("EndDatepickerdialog")){
            endDate.setText(year+"/"+monthOfYear+"/"+dayOfMonth);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        if(this.getFragmentManager().findFragmentByTag("StartTimepickerdialog") != null){
            startTime.setText(hourOfDay+":"+minute);
        }else if(this.getFragmentManager().findFragmentByTag("EndTimepickerdialog") != null){
            endTime.setText(hourOfDay+":"+minute);
        }
    }
    public void onStartDateClicked(View v){

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                BoardAssignmentActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "StartDatepickerdialog");

    }
    public void onStartTimeClicked(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                BoardAssignmentActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        dpd.show(getFragmentManager(), "StartTimepickerdialog");

    }
    public void onEndDateClicked(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                BoardAssignmentActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "EndDatepickerdialog");
    }
    public void onEndTimeClicked(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog dpd = TimePickerDialog.newInstance(
                BoardAssignmentActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        dpd.show(getFragmentManager(), "EndTimepickerdialog");

    }
    private boolean isAnyInputExist()
    {
        if(titleEditText.getText().toString().equals("")
                && contentEditText.getText().toString().equals("")){
            return false;
        }
        return true;
    }
    private void setOptionMenuSyncChanged()
    {
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidateOptionsMenu();
            }
        });
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidateOptionsMenu();
            }
        });
    }
    private void setCurrentTime()
    {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy/MM/dd",java.util.Locale.getDefault());
        SimpleDateFormat curTimeFormat = new SimpleDateFormat("HH:MM", java.util.Locale.getDefault());

        String strCurDate = curDateFormat.format(date);
        String strCurTime = curTimeFormat.format(date);

        startDate.setText(strCurDate);
        startTime.setText(strCurTime);

        endDate.setText(strCurDate);
        endTime.setText(strCurTime);
    }
}
