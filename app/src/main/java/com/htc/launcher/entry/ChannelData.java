package com.htc.launcher.entry;

import java.util.ArrayList;

public class ChannelData {

    String channel_id;
    String channel_des;
    ArrayList<AppsData> apps ;
    ArrayList<AppsData> appsData ;
    int status ;

    String msg ;


    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_des(String channel_des) {
        this.channel_des = channel_des;
    }

    public String getChannel_des() {
        return channel_des;
    }

    public void setApps(ArrayList<AppsData> apps) {
        this.apps = apps;
    }

    public ArrayList<AppsData> getApps() {
        return apps;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setAppsData(ArrayList<AppsData> appsData) {
        this.appsData = appsData;
    }

    public ArrayList<AppsData> getAppsData() {
        return appsData;
    }
}
