package com.example.kjpark.smartclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
public class NoticeTab extends Fragment{

    private final String TAG = "NoticeTab";

    private ListView listView;
    private ListViewAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_notice, container, false);

        adapter = new ListViewAdapter(getContext());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemClickListener);

        adapter.addNotice(getResources().getDrawable(R.drawable.ic_warning)
                , "공지qwewqwqeqewqe1"
                , "2015년 11월 22일"
                ,getResources().getDrawable(R.drawable.ic_sign));

        adapter.addNotice(null
                ,"공지2"
                ,"2015년 11월 23일"
                , null);

        return view;
    }
    AdapterView.OnItemClickListener ItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), ((NoticeListData)adapter.getItem(position)).mTitle, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_write, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_write){
            Intent intent = new Intent(getActivity(), BoardNoticeActivity.class);
            getActivity().startActivity(intent);
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
    private class ListViewAdapter extends BaseAdapter{

        private Context mContext;
        private ArrayList<NoticeListData> mListData = new ArrayList<NoticeListData>();

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
                convertView = inflater.inflate(R.layout.listview_noticeitem, null);

                holder.mIcon = (ImageView) convertView.findViewById(R.id.mImage);
                holder.mTitle = (TextView) convertView.findViewById(R.id.mText);
                holder.mDate = (TextView) convertView.findViewById(R.id.mDate);
                holder.mSignButton = (ImageButton) convertView.findViewById(R.id.signImageButton);

                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            NoticeListData mData = mListData.get(position);

            holder.mIcon.setVisibility(View.VISIBLE);
            if(mData.mIcon != null){
                holder.mIcon.setImageDrawable(mData.mIcon);
            }

            if(mData.mSign != null){
                holder.mSignButton.setVisibility(View.VISIBLE);
                holder.mSignButton.setImageDrawable(mData.mSign);
            } else{
                holder.mSignButton.setVisibility(View.GONE);
            }
            if(holder.mSignButton.getVisibility() == View.VISIBLE){
                //set the click listener
                holder.mSignButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "called", Toast.LENGTH_LONG).show();
                    }
                });
            }

            holder.mTitle.setText(mData.mTitle);
            holder.mDate.setText(mData.mDate);

            return convertView;
        }
        public void addNotice(Drawable icon, String mTitle, String mDate, Drawable sign)
        {
            NoticeListData addInfo = new NoticeListData();
            addInfo.mIcon = icon;
            addInfo.mTitle = mTitle;
            addInfo.mDate = mDate;
            addInfo.mSign = sign;


            mListData.add(addInfo);
        }
        public void removeNotice(int position)
        {
            mListData.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}
