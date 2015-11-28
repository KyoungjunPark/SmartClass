package com.example.kjpark.smartclass;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.kjpark.smartclass.adapter.SignListViewAdapter;

/**
 * Created by parkk on 2015-11-29.
 */
public class SignListActivity extends Activity {
    ListView listView;
    SignListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signlist);

        listView = (ListView) findViewById(R.id.signListView);
        adapter = new SignListViewAdapter(this);
    }
}
