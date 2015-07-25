package com.erpy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class GlobalInfo {

    String saveFilePath;
    String saveThumbPath;

    public static final String CP_OKMALL = "okmall";
    public static final String CP_FIRST  = "first";
    public static final String CP_CAMPINGMALL  = "campingmall";
    public static final String CP_SBCLUB  = "sbclub";
    public static final String CP_DICAMPING  = "dicamping";
    public static final String CP_CCAMPING  = "ccamping";
    public static final String CP_CAMPINGON  = "campingon";
    public static final String CP_CampTown  = "camptown";
    public static final String CP_Aldebaran  = "aldebaran";
    public static final String CP_OMyCamping  = "omycamping";
    public static final String CP_CampI  = "campi";
    public static final String CP_Camping365  = "camping365";
    public static final String CP_LeisureMan  = "leisureman";
    public static final String CP_WeekEnders  = "weekenders";
    public static final String CP_CampingPlus  = "campingplus";
    public static final String CP_SnowPeak  = "snowpeak";
    public static final String CP_Starus  = "starus";
    public static final String CP_CampSchule  = "campschule";
    public static final String CP_TongOutdoor  = "tongoutdoor";
    public static final String CP_AirMT  = "airmt";
    public static final String CP_Gogo337  = "gogo337";
    public static final String CP_Totooutdoor  = "totooutdoor";
    public static final String CP_Niio  = "niio";
    public static final String CP_YahoCamping  = "yahocamping";
    public static final String CP_CampingAmigo  = "campingamigo";
    public static final String CP_GoodCamping  = "goodcamping";

    public static final String CP_CouPang  = "coupang";
    public static final String CP_WeMef  = "wemef";
    public static final String CP_Timon  = "timon";
    public static final String CP_G9  = "g9";
    public static final String CP_GSDeal  = "gsdeal";
    public static final String CP_LotteThanksDeal  = "lottethanksdeal";
    public static final String CP_HotKill  = "hotkill";
    public static final String CP_HappyVirusFirst  = "happyvirusfirst";
    public static final String CP_HappyVirusPost  = "happyviruspost";
    public static final String CP_HappyDeals  = "happydeals";

    public static final String UTF8 = "utf-8";
    public static final String EUCKR = "euc-kr";

    public GlobalInfo() throws IOException {
//        ClassLoader cl;
//        cl = Thread.currentThread().getContextClassLoader();
//        if (cl == null) {
//            cl = ClassLoader.getSystemClassLoader();
//        }
//        URL url = cl.getResource("crawl.properties");
//        File propFile = new File(url.getPath());
//        FileInputStream is = new FileInputStream(propFile);

        InputStream is = getClass().getClassLoader().getResourceAsStream("crawl.properties");
        Properties props = new Properties();
        props.load(is);
        saveFilePath = props.getProperty("crawl.save.prefix.path");
        saveThumbPath = props.getProperty("crawl.save.prefix.thumb.path");
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public String getSaveThumbPath() {
        return saveThumbPath;
    }
}
