package com.example.kjpark.smartclass;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kjpark.smartclass.adapter.FeedListAdapter;
import com.example.kjpark.smartclass.app.AppController;
import com.example.kjpark.smartclass.data.FeedItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.tab_memory, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        feedItems = new ArrayList<>();

        adapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(adapter);


        //getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        //getActivity().getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                parseJsonFeed(new JSONObject(data));
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL_FEED, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            AppController.getInstance().addToRequestQueue(jsonRequest);
        }


        return view;

    }
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("status"));
                item.setProfilePic(feedObj.getString("profilePic"));
                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

            // notify data changes to list adapter
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_enroll, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
