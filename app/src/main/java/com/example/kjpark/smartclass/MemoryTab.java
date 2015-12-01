package com.example.kjpark.smartclass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kjpark.smartclass.adapter.FeedListAdapter;
import com.example.kjpark.smartclass.data.FeedListData;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
public class MemoryTab extends Fragment{

    private static final String TAG = "MemoryTab";
    private ListView listView;
    private FeedListAdapter adapter;
    private List<FeedListData> feedListDatas;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";

    private static final int BOARD_MEMORY = 1000;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_memory, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        feedListDatas = new ArrayList<>();

        adapter = new FeedListAdapter(getActivity(), feedListDatas);
        listView.setAdapter(adapter);

        loadBoards();
        return view;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode) {
            case BOARD_MEMORY: {
                Log.d(TAG, "BOARD_MEMORY called");
                loadBoards();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_write){
            Intent intent = new Intent(getActivity(), BoardMemoryActivity.class);
            startActivityForResult(intent, BOARD_MEMORY);
        }
        return true;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_enroll, menu);
        if(ConnectServer.getInstance().getType() != ConnectServer.Type.teacher) {
            menu.removeItem(R.id.action_write);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    private void loadBoards() {
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private List<FeedListData> result = new ArrayList<FeedListData>();

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/board_memory");
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

                        JSONArray jsonArray = new JSONArray(tmpString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Integer num = (Integer) object.get("num");
                            String name = (String) object.get("name");
                            String content = (String) object.get("content");
                            String image_data = (String) object.get("image");
                            String profilePic_data = (String) object.get("profilePic");

                            byte[] b;
                            Bitmap image = null;
                            if (!image_data.equals("")) {
                                b = Base64.decode(image_data, Base64.DEFAULT);
                                image = BitmapFactory.decodeByteArray(b, 0, b.length);
                            }
                            Bitmap profilePic = null;
                            if(!profilePic_data.equals("")) {
                                b = Base64.decode(profilePic_data, Base64.DEFAULT);
                                profilePic = BitmapFactory.decodeByteArray(b, 0, b.length);
                            }
                            String time = (String) object.get("time");

                            result.add(new FeedListData(num, name, content, image, profilePic, time));
                        }
                        Log.d("---- success ----", tmpString);
                    } else {
                        rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                        Log.d("---- failed ----", String.valueOf(rd.readLine()));
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean value) {
                feedListDatas = new ArrayList<>();
                adapter = new FeedListAdapter(getActivity(), feedListDatas);
                listView.setAdapter(adapter);
                for (int i = 0; i < result.size(); i++) {
                    adapter.addFeed(result.get(i).getNum()
                            , result.get(i).getName(), result.get(i).getContent()
                            , result.get(i).getImage(), result.get(i).getProfilePic(), result.get(i).getTime());
                }

                Log.d(TAG, "notify called");
                adapter.notifyDataSetChanged();
            }
        });
        ConnectServer.getInstance().execute();
    }
}
