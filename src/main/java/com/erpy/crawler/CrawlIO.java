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

        if (this.path.length() <= 0) {
            System.out.println("ERROR :: file path is NOT");
            return false;
        }

        //String buffer = new String(this.data.getBytes("euc-kr"), "utf-8");

        //BufferedWriter bw = new BufferedWriter(new FileWriter(this.path));
        File file = new File(this.path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), this.encoding));
        bw.write(this.data);
        bw.close();

        String log = String.format("frush file ... size(%s), encoding(%s), path(%s)",
                this.data.length(), this.encoding, this.path);
        System.out.println(log);

        return true;
    }
}
