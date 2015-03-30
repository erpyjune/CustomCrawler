package com.erpy.utils;

import org.apache.ibatis.io.Resources;

import java.io.*;
import java.net.URL;
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
