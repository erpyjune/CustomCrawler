package com.erpy.utils;

import java.io.IOException;

import com.erpy.crawler.HtmlParser;

/**
 * Created by baeonejune on 14. 12. 1..
 */
public class Parser {
    public static void main (String args[]) throws IOException {

        HtmlParser hp = new HtmlParser();
        hp.setReadFilePath("/Users/baeonejune/work/SummaryNode/out1.html");
        hp.readParseData();
        hp.htmlParseData();

        System.out.println("end!!");
    }
}
