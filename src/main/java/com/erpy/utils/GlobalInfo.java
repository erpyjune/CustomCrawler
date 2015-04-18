package com.erpy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class GlobalInfo {

    String saveFilePath;

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

    public static final String CP_CooPang  = "coopang";
    public static final String CP_WeMef  = "wemef";
    public static final String CP_Timon  = "timon";
    public static final String CP_G9  = "g9";

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
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }
}
