package com.erpy.io;

import java.io.*;

/**
 * Created by baeonejune on 14. 12. 26..
 */
public class FileIO {
    String data;
    String path;
    String encoding="euc-kr";

    public FileIO() {}

    public FileIO(String path, String encoding) {
        this.path = path;
        this.encoding = encoding;
        this.data=null;
    }

    public String getFileContent() throws IOException {
        String buffer;
        StringBuilder content = new StringBuilder();
        File file = new File(this.path);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),this.encoding));
        while((buffer = br.readLine()) != null) {
            content.append(buffer);
        }

        br.close();

        this.data = content.toString();

        return this.data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
