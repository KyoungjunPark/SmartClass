package com.example.kjpark.smartclass;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by parkk on 2015-11-20.
 */

public class JoinTeacherActivity extends AppCompatActivity {

    private final static String TAG = "JoinTeacherActivity";
    private Toolbar toolbar;

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ImageButton manImageButton;
    private ImageButton womanImageButton;
    private Button createButton;

    private boolean isManClicked = false;
    private boolean isWomanClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_teacher);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.joinTeacher);
        setToolbar();

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        manImageButton = (ImageButton) findViewById(R.id.manImageButton);
        womanImageButton = (ImageButton) findViewById(R.id.womanImageButton);
        createButton = (Button) findViewById(R.id.createButton);
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

    }

}