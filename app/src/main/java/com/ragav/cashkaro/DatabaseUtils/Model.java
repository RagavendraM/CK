package com.ragav.cashkaro.DatabaseUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "model_table")
public class Model {
    @PrimaryKey (autoGenerate=true)
    int uid;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "img_url")
    String imgUrl;

    @ColumnInfo(name = "time_shared")
    String timeShared;

    @ColumnInfo(name = "shared_via")
    String app;

    public String getApp() {
        return app;
    }

    public int getUid() {
        return uid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTimeShared() {
        return timeShared;
    }

    public String getTitle() {
        return title;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTimeShared(String timeShared) {
        this.timeShared = timeShared;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
