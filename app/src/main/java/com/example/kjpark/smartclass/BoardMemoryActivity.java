package com.example.kjpark.smartclass;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kjpark.smartclass.utils.ConnectServer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by parkk on 2015-11-17.
 */

/*
 * Copyright 2015 Wouter Dullaert. All rights reserved.
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
public class BoardMemoryActivity extends AppCompatActivity{

    private final String TAG = "BoardMemoryActivity";
    private Toolbar toolbar;

    private EditText contentEditText;
    private ImageView imageImageView;
    private ImageButton cameraImageButton;
    private Bitmap imageBitmap;

    private static final int PICK_FROM_GALLERY = 1000;
    private static final int PICK_FROM_CAMERA = 1001;
    private static final int CROP_FROM_CAMERA = 1002;

    private Uri mImageCaptureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memory);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setToolbar();

        contentEditText = (EditText) findViewById(R.id.contentEditText);
        imageImageView = (ImageView) findViewById(R.id.imageImageView);
        cameraImageButton = (ImageButton) findViewById(R.id.cameraImageButton);

        imageBitmap = null;

        setOptionMenuSyncChanged();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode) {
            case CROP_FROM_CAMERA: {
                String filePath = Environment.getExternalStorageDirectory()
                        + "/temporary_holder.jpg";

                imageBitmap = BitmapFactory.decodeFile(filePath);

                File f = new File(Environment.getExternalStorageDirectory(),
                        "/temporary_holder.jpg");
                if (f.exists())
                    f.delete();

                imageImageView.setImageBitmap(imageBitmap);

                break;
            }
            case PICK_FROM_GALLERY: {
                mImageCaptureUri = data.getData();
                /*
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageImageView.setImageBitmap(imageBitmap);

                break;
                */
            }
            case PICK_FROM_CAMERA: {
                try {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(mImageCaptureUri, "image/*");

                    intent.putExtra("outputX", 800);
                    intent.putExtra("outputY", 800);
                    intent.putExtra("aspectX", 4);
                    intent.putExtra("aspectY", 3);
                    intent.putExtra("scale", true);
                    intent.putExtra("crop", "true");
                    intent.putExtra("return-data", true);

                    File f = new File(Environment.getExternalStorageDirectory(),
                            "/temporary_holder.jpg");
                    try {
                        f.createNewFile();
                    } catch (IOException ex) {
                        Log.e("io", ex.getMessage());
                    }

                    Uri uri = Uri.fromFile(f);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    startActivityForResult(intent, CROP_FROM_CAMERA);

                } //respond to users whose devices do not support the crop action
                catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notice_board, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_enroll);

        if(isAnyInputExist()) {
            item.setEnabled(true);
        }else {
            item.setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_enroll){
            if(isAnyInputExist()){
                //send to server the name/ info/ date
                ConnectServer.getInstance().setAsncTask(new AsyncTask<String, Void, Boolean>() {
                    private String requestMessage;
                    private int requestCode;
                    private String content = contentEditText.getText().toString();
                    @Override
                    protected Boolean doInBackground(String... params) {
                        URL obj = null;
                        try {
                            obj = new URL("http://165.194.104.22:5000/enroll_memory");

                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            //implement below code if token is send to server
                            con = ConnectServer.getInstance().setHeader(con);

                            con.setDoOutput(true);

                            String image = "";
                            if(imageBitmap != null) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                                byte[] b = baos.toByteArray();
                                image = Base64.encodeToString(b, Base64.DEFAULT);
                                Log.d(TAG, "image: " + image.length());
                            }

                            String parameter = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
                            parameter += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");

                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(parameter);
                            wr.flush();
                            BufferedReader rd = null;

                            requestCode = con.getResponseCode();
                            if (requestCode == 200) {
                                //enroll success

                            } else {
                                // enroll fail
                                rd = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));

                                requestMessage = rd.readLine();
                                Log.d("----- server -----", String.valueOf(rd.readLine()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        AlertDialog dialog = createDialogBox(requestCode, requestMessage);
                        dialog.show();
                    }
                });
                ConnectServer.getInstance().execute();

            }
        }

        return true;
    }
    private AlertDialog createDialogBox(int requestCode, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (requestCode == 200) {
            Toast.makeText(getApplicationContext(), "등록하였습니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            builder.setTitle("등록 실패");

            // 에러 메시지 전송
            builder.setMessage("서버와의 통신이 원활하지 않습니다.\n 다음에 다시 시도해 주세요." + "\n");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
        }

        AlertDialog dialog = builder.create();
        return dialog;

    }

    private void setToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_close);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if user typing any words.. then handling it!
                if (isAnyInputExist()) {
                    //question that are you really want to go out
                    AlertDialog.Builder builder = new AlertDialog.Builder(BoardMemoryActivity.this);
                    builder.setTitle("변경을 취소하시겠어요?");
                    builder.setMessage("지금 돌아가면 작성 중인 내용이 취소됩니다.");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("유지", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    //go out directly
                    onBackPressed();
                }
            }
        });
    }
    private boolean isAnyInputExist()
    {
        if(contentEditText.getText().toString().equals("")){
            return false;
        }
        return true;
    }
    private void setOptionMenuSyncChanged()
    {
        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                invalidateOptionsMenu();
            }
        });
    }
    public void onCameraImageButtonClicked(View v)
    {
        CharSequence selections[] = new CharSequence[] {"사진 앨범","카메라"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진선택");
        builder.setItems(selections, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //the case: 사진 앨범
                    getPhotoFromGallery();
                } else if (which == 1) {
                    //the case: 카메라
                    getPhotoFromCamera();
                }
            }
        });
        builder.show();
    }
    private void getPhotoFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }
    private void getPhotoFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpeg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

}
