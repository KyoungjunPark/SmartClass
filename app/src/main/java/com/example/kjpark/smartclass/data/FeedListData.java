package com.example.kjpark.smartclass.data;

import android.graphics.Bitmap;

/**
 * Created by parkk on 2015-11-23.
 */
public class FeedListData {
    private int num;

    private String name, content, time;
    private Bitmap image, profilePic;

    public FeedListData() {
    }

    public FeedListData(int num, String name, String content, Bitmap image,
                        Bitmap profilePic, String time) {
        super();
        this.num = num;
        this.name = name;
        this.content = content;
        this.image = image;
        this.profilePic = profilePic;
        this.time = time;
    }
    public void setNum(int num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImage() {
        return image;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public String getTime() {
        return time;
    }


}
