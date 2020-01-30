package com.mtk.bean;

public class SettingsBean {
    private int imgResId;
    private Boolean isHaveTg = Boolean.valueOf(false);
    private Boolean isTgStatus;
    private int tvDescriptionResId;
    private int tvTitleResId;

    public int getImgResId() {
        return this.imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public int getTvTitleResId() {
        return this.tvTitleResId;
    }

    public void setTvTitleResId(int tvTitleResId) {
        this.tvTitleResId = tvTitleResId;
    }

    public int getTvDescriptionResId() {
        return this.tvDescriptionResId;
    }

    public void setTvDescriptionResId(int tvDescriptionResId) {
        this.tvDescriptionResId = tvDescriptionResId;
    }

    public Boolean getTgSatus() {
        return this.isTgStatus;
    }

    public void setTgSatus(Boolean tgStatus) {
        this.isTgStatus = tgStatus;
    }

    public Boolean getHaveTg() {
        return this.isHaveTg;
    }

    public void setHaveTg(Boolean haveTg) {
        this.isHaveTg = haveTg;
    }
}
