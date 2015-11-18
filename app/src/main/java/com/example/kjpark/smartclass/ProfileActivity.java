/*
 * Copyright (c) 2015. Kyoungjun Park. All Rights Reserved.
 */

package com.example.kjpark.smartclass;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView emailTextView;

    private RelativeLayout profileImageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.profile);
        setToolbar();

        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        profileImageLayout = (RelativeLayout) findViewById(R.id.profileImageLayout);

        resizeImage();

        setBackgroundImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
        menu.removeItem(R.id.action_user);
        menu.removeItem(R.id.action_write);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.left_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void resizeImage()
    {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        int width = (int) (getWindowManager().getDefaultDisplay().getWidth());
        int height = (int) (getWindowManager().getDefaultDisplay().getHeight()*0.8);
        Bitmap resizedBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
        profileImageView.setImageBitmap(resizedBmp);
    }
    private void setBackgroundImage()
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        int width = (int) (getWindowManager().getDefaultDisplay().getWidth());
        int height = (int) (getWindowManager().getDefaultDisplay().getHeight()*0.8);

        Bitmap bmp = decodeSampledBitmapFromResource(getResources(), R.drawable.profile_background,width, height);
        Bitmap resizedBmp = Bitmap.createScaledBitmap(bmp, width, height, true);
        BitmapDrawable bmpDrawable = new BitmapDrawable(getResources(), resizedBmp);
        profileImageLayout.setBackground(bmpDrawable);
        profileImageLayout.

    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
