package com.example.kjpark.smartclass;

import android.content.Context;
import android.content.Intent;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kjpark.smartclass.data.SettingListData;
import com.example.kjpark.smartclass.services.GetPhoneStateService;
import com.example.kjpark.smartclass.services.PhoneStateService;
import com.example.kjpark.smartclass.utils.ConnectServer;

import java.util.ArrayList;

/**
 * Created by KJPARK on 2015-11-16.
 *
 * @since 0.1
 */
public class SettingTab extends Fragment{

    private final String TAG = "SettingTab";

    private ListView listView;
    private ListViewAdapter adapter;

    private Intent serviceIntent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_setting, container, false);

        adapter = new ListViewAdapter(getContext());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemClickListener);

        adapter.addNotice("버전정보"
                , "1.0.0", null);
        adapter.addNotice("코드정보"
                , null, null);
        if(ConnectServer.getInstance().getType() == ConnectServer.Type.teacher) {
            adapter.addNotice("집중모드"
                    ,null ,true);
        }
        return view;
    }
    AdapterView.OnItemClickListener ItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if(holder.mTitle.getText().toString().equals("버전정보")){
                Intent intent = new Intent(getActivity(), VersionInfoActivity.class);
                startActivity(intent);
            } else if (holder.mTitle.getText().toString().equals("코드정보")) {
                if(ConnectServer.getInstance().getType() == ConnectServer.Type.teacher) {
                    Intent intent = new Intent(getActivity(), CodeInfoTeacherActivity.class);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(getActivity(), CodeInfoActivity.class);
                    startActivity(intent);
                }
            } else if (holder.mTitle.getText().toString().equals("집중모드")){
                ToggleButton toggleButton = (ToggleButton) view.findViewById(R.id.toggleButton);
                toggleButton.setSelected(!holder.mToggleButton.isSelected());
                holder.mToggleButton.setSelected(!holder.mToggleButton.isSelected());
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_user){
            Log.d(TAG, "ProfileActivity called");
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        }


        return true;
    }

    public class ViewHolder
    {
        public TextView mTitle;
        public TextView mInfo;
        public ToggleButton mToggleButton;
    }
    private class ListViewAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<SettingListData> mListData = new ArrayList<SettingListData>();

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

            if(convertView == null){
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_settingitem, null);

                holder.mTitle = (TextView) convertView.findViewById(R.id.mText);
                holder.mInfo = (TextView) convertView.findViewById(R.id.mInfo);
                holder.mToggleButton = (ToggleButton) convertView.findViewById(R.id.toggleButton);

                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            SettingListData mData = mListData.get(position);

            holder.mTitle.setText(mData.mTitle);

            if(mData.mInfo != null) holder.mInfo.setText(mData.mInfo);
            //else holder.mInfo.setVisibility(View.GONE);

            if(mData.mIsConcentrationMode != null) {
                holder.mToggleButton.setSelected(mData.mIsConcentrationMode);
                holder.mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            Log.d(TAG,"mode start");
                            serviceIntent = new Intent(getActivity(), GetPhoneStateService.class);
                            getActivity().startService(serviceIntent);
                        } else {
                            Log.d(TAG,"mode stop");
                            getActivity().stopService(serviceIntent);
                        }
                    }
                });
            }
            else holder.mToggleButton.setVisibility(View.GONE);

            return convertView;
        }
        public void addNotice(String mTitle, String mInfo, Boolean isConcentrationMode)
        {
            SettingListData addInfo = new SettingListData();
            addInfo.mTitle = mTitle;
            addInfo.mInfo = mInfo;
            addInfo.mIsConcentrationMode = isConcentrationMode;
            mListData.add(addInfo);
        }
        public void removeNotice(int position)
        {
            mListData.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}
