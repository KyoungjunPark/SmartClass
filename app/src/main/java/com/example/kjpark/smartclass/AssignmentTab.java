package com.example.kjpark.smartclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.kjpark.smartclass.data.AssignmentListData;
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
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
public class AssignmentTab extends Fragment{
    private final String TAG = "AssignmentTab";

    private ListView listView;
    private ListViewAdapter adapter;
    private static final int BOARD_ASSIGNMENT = 1000;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        switch(requestCode) {
            case BOARD_ASSIGNMENT: {
                Log.d(TAG,"BOARD_ASSIGNMENT called");
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
                            parameter += "&" + URLEncoder.encode("board_type", "UTF-8") + "=" + URLEncoder.encode("assignment", "UTF-8");


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

        View view = inflater.inflate(R.layout.tab_assignment, container, false);

        adapter = new ListViewAdapter(getContext());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemClickListener);

        loadBoards();
        return view;
    }
    AdapterView.OnItemClickListener ItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater dialogInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View assignmentDialogView = dialogInflater.inflate(R.layout.dialog_assignmentitem, null);

            TextView title = (TextView) assignmentDialogView.findViewById(R.id.titleTextView);
            TextView content = (TextView) assignmentDialogView.findViewById(R.id.contentTextView);
            TextView date = (TextView) assignmentDialogView.findViewById(R.id.dateTextView);

            title.setText(adapter.mListData.get(position).mTitle);
            content.setText(adapter.mListData.get(position).mContent);
            date.setText(adapter.mListData.get(position).mStart_date + " ~ " + adapter.mListData.get(position).mEnd_date);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("과제방");
            builder.setView(assignmentDialogView);

            Button checkButton = (Button) assignmentDialogView.findViewById(R.id.checkButton);

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
            Intent intent = new Intent(getActivity(), BoardAssignmentActivity.class);
            startActivityForResult(intent, BOARD_ASSIGNMENT);
        }


        return true;
    }
    public class ViewHolder
    {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mDate;
    }
    private class ListViewAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<AssignmentListData> mListData = new ArrayList<AssignmentListData>();

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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_assignmentitem, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mTitle = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AssignmentListData mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);
            if (mData.isImportant) {
                holder.mIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_warning));
            }

            holder.mTitle.setText(mData.mTitle);
            holder.mDate.setText(mData.mStart_date + " ~ " + mData.mEnd_date);

            return convertView;
        }
        public void addAssignment(String mTitle, String mContent, String mStart_date, String mEnd_date, Boolean isImportant)
        {
            AssignmentListData addInfo = new AssignmentListData();
            addInfo.mTitle = mTitle;
            addInfo.mContent = mContent;
            addInfo.mStart_date = mStart_date;
            addInfo.mEnd_date = mEnd_date;
            addInfo.isImportant = isImportant;

            mListData.add(addInfo);
        }
        public void removeAssignment(int position)
        {
            mListData.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
    private void loadBoards() {
        ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
            private List<AssignmentListData> result = new ArrayList<AssignmentListData>();

            @Override
            protected Boolean doInBackground(String... params) {
                URL obj = null;
                try {
                    obj = new URL("http://165.194.104.22:5000/board_assignment");
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
                            String start_date = (String) object.get("start_date");
                            String end_date = (String) object.get("end_date");
                            Boolean isImportant = ((Integer) object.get("isImportant") != 0);

                            result.add(new AssignmentListData(title, content, start_date, end_date, isImportant));
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
                for (int i = 0; i < result.size(); i++) {
                    adapter.addAssignment(result.get(i).mTitle
                            , result.get(i).mContent, result.get(i).mStart_date
                            , result.get(i).mEnd_date, result.get(i).isImportant);
                }
                Log.d(TAG, "notify called");
                adapter.notifyDataSetChanged();
            }

        });
        ConnectServer.getInstance().execute();
    }
}
