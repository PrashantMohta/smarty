package com.mtk.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppDetail {
    private String advertimage;
    private String apkfile;
    private String content;
    private long currloadsize;
    private long downcount;
    private String icon;
    public int id;
    private List<HashMap<String, String>> images;
    private int integral;
    private String name;
    private long size;
    private String title;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDowncount() {
        return this.downcount;
    }

    public void setDowncount(long downcount) {
        this.downcount = downcount;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getApkfile() {
        return this.apkfile;
    }

    public void setApkfile(String apkfile) {
        this.apkfile = apkfile;
    }

    public List<HashMap<String, String>> getImages() {
        return this.images;
    }

    public void setImages(ArrayList<HashMap<String, String>> images) {
        this.images = images;
    }

    public int getIntegral() {
        return this.integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getAdvertimage() {
        return this.advertimage;
    }

    public void setAdvertimage(String advertimage) {
        this.advertimage = advertimage;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCurrloadsize() {
        return this.currloadsize;
    }

    public void setCurrloadsize(long currloadsize) {
        this.currloadsize = currloadsize;
    }

    public void setImages(List<HashMap<String, String>> images) {
        this.images = images;
    }
}
