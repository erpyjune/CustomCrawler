package com.erpy.crawler;

import org.apache.commons.codec.binary.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by baeonejune on 14. 11. 30..
 */
public class CrawlIO {
    private String data;
    private String path;

    public void setSaveDataInfo(String saveData, String saveFilePath) {
        this.data = saveData;
        this.path = saveFilePath;
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

        BufferedWriter bw = new BufferedWriter(new FileWriter(this.path));
        bw.write(this.data);
        bw.close();

        String log = String.format("size %s, path : %s", this.data.length(), this.path);
        System.out.println(log);

        return true;
    }
}
