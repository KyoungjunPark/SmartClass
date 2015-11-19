package com.example.kjpark.smartclass;

import android.os.AsyncTask;

import java.net.HttpURLConnection;

/**
 * Created by parkk on 2015-11-20.
 */
public class ConnectServer {
    private AsyncTask<String, Void, Boolean> task;
    private String token;

    private static final ConnectServer instance = new ConnectServer();

    private ConnectServer(){}
    public static ConnectServer getInstance(){
        return instance;
    }
    public void setAsncTask(AsyncTask<String, Void, Boolean> task) {
        this.task = task;
    }
    public void setToken(String token){this.token = token;}
    public void execute(){
        this.task.execute();
    }

    public HttpURLConnection setHeader(HttpURLConnection con){
        con.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
        con.setRequestProperty("token",this.token);

        return con;
    }
}

