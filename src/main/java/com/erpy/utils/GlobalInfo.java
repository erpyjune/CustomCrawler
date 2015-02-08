package com.erpy.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class GlobalInfo {

    String saveFilePath;

    public static final String CP_OKMALL = "okmall";

    public GlobalInfo() throws IOException {
        ClassLoader cl;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }

        URL url = cl.getResource("crawl.properties");
        File propFile = new File(url.getPath());
        FileInputStream is = new FileInputStream(propFile);
        Properties props = new Properties();
        props.load(is);
        this.saveFilePath = props.getProperty("crawl.save.prefix.path");
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }
}
