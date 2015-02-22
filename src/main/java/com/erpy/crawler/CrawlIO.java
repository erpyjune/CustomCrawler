package com.erpy.crawler;

import org.apache.commons.codec.binary.StringUtils;

import java.io.*;

/**
 * Created by baeonejune on 14. 11. 30..
 */
public class CrawlIO {
    private String data;
    private String path;
    private String encoding = "euc-kr";

    public void setSaveDataInfo(String saveData, String saveFilePath, String encoding) {
        this.data = saveData;
        this.path = saveFilePath;
        this.encoding = encoding;
    }

    public String getPath() {
        return this.path;
    }
    public String getSaveData() {
        return this.data;
    }

    public boolean executeSaveData() throws IOException {

        if (path.length() <= 0) {
            System.out.println("ERROR :: file path is NOT");
            return false;
        }

        File file = new File(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), encoding));
        bw.write(data);
        bw.close();

        return true;
    }
}
