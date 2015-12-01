package com.example.kjpark.smartclass;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by USER on 2015-11-03.
 */
public class UserSettingInfo {
    private AsyncTask<String, Void, Boolean> task;

    private String ID;
    private String phoneNumber;
    private String serialNumber;
    private String alarmType;

    private static final UserSettingInfo instance = new UserSettingInfo();

    public UserSettingInfo() {
        this.alarmType = "pushalarm";
    }

    public static UserSettingInfo getInstance() {
        return instance;
    }

    public void setAsncTask(AsyncTask<String, Void, Boolean> task) {
        this.task = task;
    }

    public void execute() {
        this.task.execute();
    }

    public void setID(String id) {
        this.ID = id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        Log.d("---- set number ----", this.phoneNumber);
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getID() {
        return ID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getAlarmType() {
        return alarmType;
    }

}
