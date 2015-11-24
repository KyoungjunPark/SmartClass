package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kjpark.smartclass.data.AssignmentListData;
import com.example.kjpark.smartclass.utils.ConnectServer;

import java.util.ArrayList;

/**
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
public class AssignmentTab extends Fragment{
    private final String TAG = "AssignmentTab";

    private ListView listView;
    private ListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_assignment, container, false);

        adapter = new ListViewAdapter(getContext());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemClickListener);

        adapter.addAssignment(getResources().getDrawable(R.drawable.ic_warning)
                , "과제1"
                , "2015/11/22 ~ 2015/11/23");

        adapter.addAssignment(null
                , "과제2"
                , "2015/11/22 ~ 2015/11/23");

        return view;
    }
    AdapterView.OnItemClickListener ItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LayoutInflater dialogInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View noticeDialogView = dialogInflater.inflate(R.layout.dialog_assignmentitem, null);


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("과제방");
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
            Intent intent = new Intent(getActivity(), BoardAssignmentActivity.class);
            getActivity().startActivity(intent);
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

            if(convertView == null){
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_assignmentitem, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mTitle = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);

                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            AssignmentListData mData = mListData.get(position);


            holder.mIcon.setVisibility(View.VISIBLE);
            if(mData.mIcon != null){
                holder.mIcon.setImageDrawable(mData.mIcon);
            }

            holder.mTitle.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);

            return convertView;
        }
        public void addAssignment(Drawable icon, String mTitle, String mDate)
        {
            AssignmentListData addInfo = new AssignmentListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;

            mListData.add(addInfo);
        }
        public void removeAssignment(int position)
        {
            mListData.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}
