package com.example.kjpark.smartclass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import com.example.kjpark.smartclass.adapter.SignListViewAdapter;
import com.example.kjpark.smartclass.data.SignListData;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkk on 2015-11-29.
 */
public class SignListActivity extends Activity {
    private final static String TAG = "SignListActivity";
    ListView listView;
    SignListViewAdapter adapter;

    private int currentViewItem;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signlist);

        listView = (ListView) findViewById(R.id.signListView);
        adapter = new SignListViewAdapter(this);

        currentViewItem = getIntent().getIntExtra("position", -1);
        getSignList();
    }
    private void getSignList()
    {
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private List<SignListData> result = new ArrayList<SignListData>();
            private String num = Integer.toString(currentViewItem);

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/images/signs");
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    //implement below code if token is send to server
                    con = ConnectServer.getInstance().setHeader(con);

                    con.setDoOutput(true);
                    String parameter = URLEncoder.encode("num", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8");

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(parameter);
                    wr.flush();

                    BufferedReader rd = null;

                    if (con.getResponseCode() == 200) {
                        // 정보 가져오기
                        rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                        String tmpString = rd.readLine();

                        Log.d(TAG, "loaded data: " + tmpString);
                        JSONArray jsonArray = new JSONArray(tmpString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            String email_parent = (String) object.get("email_parent");
                            String sign_image = (String) object.get("sign_image");
                            byte[] b = Base64.decode(sign_image, Base64.DEFAULT);
                            Bitmap signBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

                            result.add(new SignListData(email_parent ,signBitmap));

                            Log.d("---- success ----", rd.toString());

                        }
                    }else{
                            // 로그인 실패
                            rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));

                            Log.d("---- failed ----", String.valueOf(rd.readLine()));
                    }
                }catch(IOException | JSONException e){
                        e.printStackTrace();
                }
                return null;
                }

                @Override
                protected void onPostExecute (Boolean aBoolean){
                    adapter = new SignListViewAdapter(getApplicationContext());
                    listView.setAdapter(adapter);
                    for (int i = 0; i < result.size(); i++) {
                        adapter.addNotice(result.get(i).mName
                                , result.get(i).mSignImage);
                    }
                    Log.d(TAG, "notify called");
                    adapter.notifyDataSetChanged();
                }
            });
        ConnectServer.getInstance().execute();

        }
    }
