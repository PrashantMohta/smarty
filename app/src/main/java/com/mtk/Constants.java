package com.mtk;

import android.os.Environment;
import java.io.File;

public final class Constants {
    public static final String APPID = "1105437308";
    public static final String APPWallPosID = "1010118218924909";
    public static final int APP_ICON_HEIGHT = 40;
    public static final int APP_ICON_WIDTH = 40;
    public static final String BannerPosID = "7070513208420917";
    public static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
    public static final String DIR_APP_MAIN = (SD_CARD_PATH + "/SmartWatch");
    public static final String DIR_APP_CACHE = (DIR_APP_MAIN + "app/");
    public static final int DOWN_ERROR = 4;
    public static final String GET_APPLIST_URL = "http://api.ruanan.com/phone/reslist.json";
    public static final int GET_UNDATAINFO_ERROR = 2;
    public static final int GET_UNDATAINFO_SUCCESS = 3;
    public static final String IMAGE_CACHE_PATH = (DIR_APP_CACHE + "imageloader/Cache");
    public static final String InterteristalPosID = "4030817218932070";
    public static final int NOTIFYMINIHEADERLENTH = 8;
    public static final int NOTIFYSYNCLENTH = 4;
    public static final String NULL_TEXT_NAME = "(unknown)";
    public static final String SplashPosID = "4070017258239014";
    public static final int TEXT_MAX_LENGH = 256;
    public static final String TEXT_POSTFIX = "...";
    public static final int TICKER_TEXT_MAX_LENGH = 128;
    public static final int TITLE_TEXT_MAX_LENGH = 128;
    public static final int UPDATA_CLIENT = 1;
    public static final int UPDATA_NONEED = 0;
    public static final String UPDATE_APP_F__URL_FOREIGN = "";
    public static final String UPDATE_APP_URL_CHINA = "";

    static {
        new File(DIR_APP_CACHE).mkdirs();
    }
}
