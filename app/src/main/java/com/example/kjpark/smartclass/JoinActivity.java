/*
 * Copyright (c) 2015. Kyoungjun Park. All Rights Reserved.
 */

package com.example.kjpark.smartclass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class JoinActivity extends AppCompatActivity {

    ImageButton teacherButton;
    ImageButton studentButton;
    ImageButton parentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        teacherButton = (ImageButton) findViewById(R.id.teacherButton);
        studentButton = (ImageButton) findViewById(R.id.studentButton);
        parentButton = (ImageButton) findViewById(R.id.parentButton);


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
    public void onTeacherButtonClicked(View v){
        Intent intent = new Intent(JoinActivity.this, JoinTeacherActivity.class);
        startActivity(intent);
    }
    public void onStudentButtonClicked(View v){
        Intent intent = new Intent(JoinActivity.this, JoinStudentActivity.class);
        startActivity(intent);
    }
    public void onParentButtonClicked(View v){
        Intent intent = new Intent(JoinActivity.this, JoinParentActivity.class);
        startActivity(intent);

    }

}
