package com.htc.launcher.entry;

import java.io.Serializable;

public class AppsData implements Serializable {

    String app_id;
    String app_name;
    String app_package;
    String app_des;
    String app_icon;
    String app_type;
    String app_zone;
    String app_size;
    String app_version;
    String app_version_des;
    String app_md5;
    String app_path;

    String app_time;

    String app_developer;

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_des(String app_des) {
        this.app_des = app_des;
    }

    public String getApp_des() {
        return app_des;
    }

    public void setApp_icon(String app_icon) {
        this.app_icon = app_icon;
    }

    public String getApp_icon() {
        return app_icon;
    }

    public void setApp_md5(String app_md5) {
        this.app_md5 = app_md5;
    }

    public String getApp_md5() {
        return app_md5;
    }

    public void setApp_path(String app_path) {
        this.app_path = app_path;
    }

    public String getApp_path() {
        return app_path;
    }

    public void setApp_size(String app_size) {
        this.app_size = app_size;
    }

    public String getApp_size() {
        return app_size;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_zone(String app_zone) {
        this.app_zone = app_zone;
    }

    public String getApp_zone() {
        return app_zone;
    }

    public void setApp_developer(String app_developer) {
        this.app_developer = app_developer;
    }

    public String getApp_developer() {
        return app_developer;
    }

    public void setApp_time(String app_time) {
        this.app_time = app_time;
    }

    public String getApp_time() {
        return app_time;
    }

    public void setApp_version_des(String app_version_des) {
        this.app_version_des = app_version_des;
    }

    public String getApp_version_des() {
        return app_version_des;
    }

}
