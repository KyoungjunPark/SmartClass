package com.example.kjpark.smartclass.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.kjpark.smartclass.FeedImageView;
import com.example.kjpark.smartclass.R;
import com.example.kjpark.smartclass.app.AppController;
import com.example.kjpark.smartclass.data.FeedListData;
import com.example.kjpark.smartclass.data.NoticeListData;

import java.util.List;

/**
 * Created by KJPARK on 2015-11-23.
 *
 * @since 0.1
 */
public class FeedListAdapter extends BaseAdapter {
    private final static String TAG = "FeedListAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedListData> feedListDatas;

    public FeedListAdapter(Activity activity, List<FeedListData> feedListDatas) {
        this.activity = activity;
        this.feedListDatas = feedListDatas;
    }


    @Override
    public int getCount() {
        return feedListDatas.size();
    }

    @Override
    public Object getItem(int location) {
        return feedListDatas.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_memoryitem, null);


        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView content = (TextView) convertView
                .findViewById(R.id.content);
        TextView time = (TextView) convertView
                .findViewById(R.id.time);
        ImageView profilePic = (ImageView) convertView
                .findViewById(R.id.profilePic);
        ImageView feedImageView = (ImageView) convertView
                .findViewById(R.id.feedImage1);

        FeedListData item = feedListDatas.get(position);

        name.setText(item.getName());
        time.setText(item.getTime());

        // Chcek for empty content message
        if (!TextUtils.isEmpty(item.getContent())) {
            content.setText(item.getContent());
            content.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            content.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageBitmap(item.getProfilePic());
        profilePic.setVisibility(View.VISIBLE);

        // Feed image
        if (item.getImage() != null) {
            feedImageView.setImageBitmap(item.getImage());
            feedImageView.setVisibility(View.VISIBLE);

        } else {
            Log.e(TAG,"FUCK!!");
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
    public void addFeed(int num, String name, String content, Bitmap image, Bitmap profilePic, String time)
    {
        FeedListData addInfo = new FeedListData();
        addInfo.setNum(num);
        addInfo.setName(name);
        addInfo.setContent(content);
        addInfo.setImage(image);
        addInfo.setProfilePic(profilePic);
        addInfo.setTime(time);

        feedListDatas.add(addInfo);
    }
}