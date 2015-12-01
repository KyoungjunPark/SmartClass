package com.example.kjpark.smartclass.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kjpark.smartclass.R;
import com.example.kjpark.smartclass.data.SignListData;

import java.util.ArrayList;

/**
 * Created by parkk on 2015-11-29.
 */
public class SignListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SignListData> mListData = new ArrayList<SignListData>();

    public SignListViewAdapter(Context mContext) {
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
            convertView = inflater.inflate(R.layout.listview_signlistitem, null);

            holder.mName = (TextView) convertView.findViewById(R.id.mName);
            holder.mIsSigned = (ImageView) convertView.findViewById(R.id.mIsSigned);

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }
        SignListData mData = mListData.get(position);

        holder.mName.setText(mData.mName);
        //holder.mIsSigned.setText("O");
        holder.mIsSigned.setImageBitmap(mData.mSignImage);
        return convertView;
    }
    public void addNotice(String mName, Bitmap sign_image)
    {
        SignListData addInfo = new SignListData();
        addInfo.mName = mName;
        addInfo.mSignImage = sign_image;
        mListData.add(addInfo);
    }
    public void removeNotice(int position)
    {
        mListData.remove(position);
    }
    public class ViewHolder
    {
        public TextView mName;
        public ImageView mIsSigned;
    }
}
