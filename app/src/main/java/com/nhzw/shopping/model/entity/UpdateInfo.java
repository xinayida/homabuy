package com.nhzw.shopping.model.entity;

import com.xinayida.lib.annotation.NotProguard;

/**
 * Created by ww on 2017/11/3.
 */

//"createTime": 1509613655530,
//"description": "",
//"forceUpdate": false,
//"os": "ANDROID",
//"update": true,
//"url": "",
//"version": 10002,
//"versionStr": "测试"
@NotProguard
public class UpdateInfo {
    public long createTime;
    public String description;
    public boolean forceUpdate;
    public String os;
    public boolean update;
    public String url;
    public String version;
    public String versionStr;

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "createTime=" + createTime +
                ", description='" + description + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", os='" + os + '\'' +
                ", update=" + update +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", versionStr='" + versionStr + '\'' +
                '}';
    }
}
