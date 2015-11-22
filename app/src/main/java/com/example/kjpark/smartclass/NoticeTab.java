package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;

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

    private final String TAG = "NoticeTab";

    private ListView listView;
    private ListViewAdapter adapter;

    private View dialogView;
    private Button clearButton;
    private Button sendButton;
    private SignaturePad mSignaturePad;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_notice, container, false);

        adapter = new ListViewAdapter(getContext());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(ItemClickListener);

        //signature layout setting
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
                //do send procedure
                /*
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if(addSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(MainActivity.this, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });
        adapter.addNotice(getResources().getDrawable(R.drawable.ic_warning)
                , "공지qwewqwqeqewqe1"
                , "2015년 11월 22일"
                , getResources().getDrawable(R.drawable.ic_sign));

        adapter.addNotice(null
                , "공지2"
                , "2015년 11월 23일"
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
                        //signature click handling
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("서명란");
                        builder.setView(dialogView);

                        AlertDialog dialog = builder.create();
                        dialog.show();

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
