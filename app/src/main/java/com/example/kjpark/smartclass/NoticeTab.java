package com.example.kjpark.smartclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kjpark.smartclass.adapter.SignListViewAdapter;
import com.example.kjpark.smartclass.data.NoticeListData;
import com.example.kjpark.smartclass.utils.ConnectServer;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
/*
 * Copyright 2014 Gianluca Cacace
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
public class NoticeTab extends Fragment{

    private final static String TAG = "NoticeTab";

    private ListView notice_listView;
    private ListViewAdapter notice_adapter;

    private ListView sign_listView;
    private SignListViewAdapter sign_adapter;

    private View dialogView;
    private Button clearButton;
    private Button sendButton;
    private SignaturePad mSignaturePad;
    private  AlertDialog signature_dialog;

    private static final int BOARD_NOTICE = 1000;

    private int currentViewItem = -1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode) {
            case BOARD_NOTICE: {
                Log.d(TAG,"BOARD_NOTICE called");
                loadBoards();
                ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
                    String title = data.getStringExtra("title");

                    @Override
                    protected Boolean doInBackground(String... params) {
                        URL obj = null;
                        try {
                            obj = new URL("http://165.194.104.22:5000/send_gcm");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            //implement below code if token is send to server
                            con = ConnectServer.getInstance().setHeader(con);

                            con.setDoOutput(true);

                            String parameter = URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                            parameter += "&" + URLEncoder.encode("board_type", "UTF-8") + "=" + URLEncoder.encode("notice", "UTF-8");


                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(parameter);
                            wr.flush();

                            if (con.getResponseCode() == 200) {
                                Log.d(TAG, "---- success ----");
                            } else {
                                Log.d(TAG, "---- failed ----");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean value) {
                    }

                });
                ConnectServer.getInstance().execute();

            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.tab_notice, container, false);

        notice_adapter = new ListViewAdapter(getContext());
        notice_listView = (ListView) view.findViewById(R.id.listView);
        notice_listView.setAdapter(notice_adapter);
        notice_listView.setOnItemClickListener(ItemClickListener);

        sign_adapter = new SignListViewAdapter(getContext());

        //signature layout setting
        setSignatureLayout();

        loadBoards();
        return view;
    }

    AdapterView.OnItemClickListener ItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater dialogInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View noticeDialogView = dialogInflater.inflate(R.layout.dialog_noticeitem, null);

            TextView title = (TextView) noticeDialogView.findViewById(R.id.titleTextView);
            TextView content = (TextView) noticeDialogView.findViewById(R.id.contentTextView);
            TextView date = (TextView) noticeDialogView.findViewById(R.id.dateTextView);

            title.setText(notice_adapter.mListData.get(position).mTitle);
            content.setText(notice_adapter.mListData.get(position).mContent);
            date.setText(notice_adapter.mListData.get(position).mDate);

            currentViewItem = position;

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("공지사항");
            builder.setView(noticeDialogView);

            Button checkButton = (Button) noticeDialogView.findViewById(R.id.checkButton);

            final AlertDialog dialog = builder.create();

            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_write, menu);
        if(ConnectServer.getInstance().getType() != ConnectServer.Type.teacher) {
            menu.removeItem(R.id.action_write);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_write){
            Intent intent = new Intent(getActivity(), BoardNoticeActivity.class);
            startActivityForResult(intent, BOARD_NOTICE);
        }


        return true;
    }

    public class ViewHolder
    {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mDate;
        public ImageButton mSignButton;
    }
    private class ListViewAdapter extends BaseAdapter {

        private Context mContext;
        public ArrayList<NoticeListData> mListData = new ArrayList<NoticeListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_noticeitem, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mTitle = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mSignButton = (ImageButton) convertView.findViewById(R.id.signImageButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NoticeListData mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);
            if (mData.isImportant) {
                holder.mIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));
            }

            if (mData.isSignNeed) {
                if(ConnectServer.getInstance().getType() == ConnectServer.Type.teacher){
                    holder.mSignButton.setVisibility(View.VISIBLE);
                    holder.mSignButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sign_list));
                } else if(ConnectServer.getInstance().getType() == ConnectServer.Type.parent){
                    if(mData.isSignFinished){
                        holder.mSignButton.setVisibility(View.VISIBLE);
                        holder.mSignButton.setEnabled(false);
                        holder.mSignButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sign_after));
                    }else{
                        holder.mSignButton.setVisibility(View.VISIBLE);
                        holder.mSignButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_sign));
                    }
                }
            } else {
                holder.mSignButton.setVisibility(View.GONE);
            }
            if (holder.mSignButton.getVisibility() == View.VISIBLE) {
                //set the click listener
                holder.mSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //signature click handling
                        currentViewItem = position;
                        if (ConnectServer.getInstance().getType() == ConnectServer.Type.teacher) {
                            Intent intent = new Intent(getContext(), SignListActivity.class);
                            intent.putExtra("position", currentViewItem);
                            startActivity(intent);
                        } else if (ConnectServer.getInstance().getType() == ConnectServer.Type.parent) {
                            setSignatureLayout();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("서명란");
                            builder.setView(dialogView);
                            signature_dialog = builder.create();
                            signature_dialog.show();
                        }
                    }
                });
            }

            holder.mTitle.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);

            return convertView;
        }

        public void addNotice(String mTitle, String mContent, String mDate, Boolean isSignNeed
                , Boolean isImportant, Boolean isSignFinished)
        {
            NoticeListData addInfo = new NoticeListData();
            addInfo.mTitle = mTitle;
            addInfo.mContent = mContent;
            addInfo.mDate = mDate;
            addInfo.isSignNeed = isSignNeed;
            addInfo.isImportant = isImportant;
            addInfo.isSignFinished = isSignFinished;

            mListData.add(addInfo);
        }

        public void removeNotice(int position) {
            mListData.remove(position);
            notice_adapter.notifyDataSetChanged();
        }
    }
    private void loadBoards() {
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private List<NoticeListData> result = new ArrayList<NoticeListData>();

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/board_notice");
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
                            String title = (String) object.get("title");
                            String content = (String) object.get("content");
                            String time = (String) object.get("time");
                            Boolean isSignNeed = ((Integer) object.get("isSignNeed") != 0);
                            Boolean isImportant = ((Integer) object.get("isImportant") != 0);
                            Boolean isSignFinished = ((Integer) object.get("isSignFinished") != 0);

                            result.add(new NoticeListData(title, time, content, isSignNeed, isImportant, isSignFinished));
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
                notice_adapter = new ListViewAdapter(getContext());
                notice_listView.setAdapter(notice_adapter);
                for (int i = 0; i < result.size(); i++) {
                    notice_adapter.addNotice(result.get(i).mTitle
                            , result.get(i).mContent, result.get(i).mDate
                            , result.get(i).isSignNeed, result.get(i).isImportant, result.get(i).isSignFinished);
                }
                Log.d(TAG, "notify called");
                notice_adapter.notifyDataSetChanged();
            }

        });
        ConnectServer.getInstance().execute();
    }
    private void setSignatureLayout()
    {
        LayoutInflater dialogInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dialogView = dialogInflater.inflate(R.layout.dialog_signature, null);
        mSignaturePad = (SignaturePad) dialogView.findViewById(R.id.signature_pad);

        clearButton = (Button) dialogView.findViewById(R.id.clearButton);
        sendButton = (Button) dialogView.findViewById(R.id.sendButton);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onSigned() {
                clearButton.setEnabled(true);
                sendButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                clearButton.setEnabled(false);
                sendButton.setEnabled(false);
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send sign image to server
                ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
                    Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                    private String num = Integer.toString(currentViewItem);

                    @Override
                    protected Boolean doInBackground(String... params) {
                        URL obj = null;
                        try {
                            obj = new URL("http://165.194.104.22:5000/enroll_sign");
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            //implement below code if token is send to server
                            con = ConnectServer.getInstance().setHeader(con);

                            con.setDoOutput(true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String sign_image = Base64.encodeToString(b, Base64.DEFAULT);
                            Log.d(TAG, "sign_image: " + sign_image.length());

                            String parameter = URLEncoder.encode("num", "UTF-8") + "=" + URLEncoder.encode(num, "UTF-8");
                            parameter += "&" + URLEncoder.encode("sign_image", "UTF-8") + "=" + URLEncoder.encode(sign_image, "UTF-8");

                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(parameter);
                            wr.flush();

                            BufferedReader rd = null;

                            if (con.getResponseCode() == 200) {
                                // 등록 성공
                                rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                                Log.d("---- success ----", rd.toString());


                            } else {
                                // 로그인 실패
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

                    }
                });
                ConnectServer.getInstance().execute();
                signature_dialog.dismiss();
                loadBoards();
            }
        });
    }
}
