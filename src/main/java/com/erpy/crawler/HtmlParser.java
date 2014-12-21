package com.erpy.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by baeonejune on 14. 12. 1..
 */
public class HtmlParser {
    private String readFilePath=null;
    private String writeFilePath=null;
    private String parseData=null;
    private String writeData=null;

    public void setReadFilePath(String path) {
        this.readFilePath = path;
    }

    public void setWriteFilePath(String path) {
        this.writeFilePath = path;
    }

    public void readParseData() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(this.readFilePath));
        StringBuffer sb = new StringBuffer();
        String s;

        while ((s = in.readLine()) != null) {
            sb.append(s);
        }

        this.parseData = sb.toString();

        in.close();
    }

    public void writeParseData() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(this.readFilePath));
        StringBuffer sb = new StringBuffer();
        String s;

        while ((s = in.readLine()) != null) {
            sb.append(s);
        }

        this.parseData = sb.toString();

        in.close();
    }

    public void htmlParseData() {
        Document doc = Jsoup.parse(this.parseData);

        Element link = doc.select("a").first();
        String text = doc.body().text(); // "An example link"
        String linkHref = link.attr("href"); // "http://example.com/"
        String linkText = link.text(); // "example""

        String linkOuterH = link.outerHtml();
        // "<a href="http://example.com"><b>example</b></a>"
        String linkInnerH = link.html(); // "<b>example</b>"


        System.out.println("linkHref : " + linkHref);
        System.out.println("linkText : " + linkText);
        System.out.println("linkOuterH : " + linkOuterH);
        System.out.println("linkInnerH : " + linkInnerH);
    }
}
