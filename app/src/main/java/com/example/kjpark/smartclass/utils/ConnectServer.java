package com.example.kjpark.smartclass.utils;

import android.os.AsyncTask;

import com.example.kjpark.smartclass.data.NoticeListData;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by parkk on 2015-11-20.
 */
public class ConnectServer {
    private AsyncTask<String, Void, Boolean> task;

    public enum Type {teacher, student, parent}
    Type type;

    private String token;
    private String code;

    private static final ConnectServer instance = new ConnectServer();

    private ConnectServer(){}
    public static ConnectServer getInstance(){ return instance;}
    public void setAsncTask(AsyncTask<String, Void, Boolean> task) { this.task = task;}
    public void setToken(String token){this.token = token;}
    public Type getType(){return type;}
    public void setTypetoTeacher() { type = Type.teacher;}
    public void setTypetoStudent()
    {
        type = Type.student;
    }
    public void setTypeParent()
    {
        type = Type.parent;
    }
    public void setCode(String code){this.code = code;}

    public void execute(){
        this.task.execute();
    }

    public HttpURLConnection setHeader(HttpURLConnection con){
        con.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
        con.setRequestProperty("token",this.token);

        return con;
    }
}

