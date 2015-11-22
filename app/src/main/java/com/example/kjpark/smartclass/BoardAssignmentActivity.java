package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

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

    private EditText titleEditText;
    private EditText contentEditText;

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

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice_board, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
            String week = getWeek(year, monthOfYear, dayOfMonth);
            startDate.setText(year+"년 "+monthOfYear+"월 "+dayOfMonth+"일 "+"("+week+")");

        }else if(view.getTag().equals("EndDatepickerdialog")){
            String week = getWeek(year, monthOfYear, dayOfMonth);
            endDate.setText(year+"년 "+monthOfYear+"월 "+dayOfMonth+"일 "+"("+week+")");
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String timeTag = null;


        if(hourOfDay >= 12){
            timeTag = "오후";
            if(hourOfDay != 12)
                hourOfDay -= 12;
        }else{
            timeTag = "오전";
        }
        if(this.getFragmentManager().findFragmentByTag("StartTimepickerdialog") != null){
            startTime.setText(timeTag + " "+hourOfDay+":"+minute);
        }else if(this.getFragmentManager().findFragmentByTag("EndTimepickerdialog") != null){
            endTime.setText(timeTag + " "+hourOfDay+":"+minute);
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
    private String getWeek(int Year, int Month, int Date)
    {
        String week = null;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month);
        cal.set(Calendar.DATE, Date);

        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case 1:
                week = "일";
                break;
            case 2:
                week = "월";
                break;
            case 3:
                week = "화";
                break;
            case 4:
                week = "수";
                break;
            case 5:
                week = "목";
                break;
            case 6:
                week = "금";
                break;
            case 7:
                week = "토";
                break;
        }
        return week;
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
}
