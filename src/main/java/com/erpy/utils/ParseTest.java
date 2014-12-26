package com.erpy.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.erpy.dao.CrawlData;
import com.erpy.parser.HtmlParser;
import com.erpy.dao.CrawlDataService;

/**
 * Created by baeonejune on 14. 12. 1..
 */
public class ParseTest {

    public static void main (String args[]) throws IOException {

        CrawlData crawlData;
        CrawlDataService crawlDataService = new CrawlDataService();

        List<CrawlData> listCrawlData = crawlDataService.getAllCrawlDatas();
        Iterator iterator = listCrawlData.iterator();
        while (iterator.hasNext()) {
            crawlData = (CrawlData)iterator.next();
            System.out.println("id: " + crawlData.getDataId());
            System.out.println("cp_name: " + crawlData.getCpName());
            System.out.println("date: " + crawlData.getCrawlDate());
            System.out.println("date: " + crawlData.getSeedUrl());
            System.out.println("path: " + crawlData.getSavePath());

        }
/*
        HtmlParser hp = new HtmlParser();
        hp.setReadFilePath("/Users/baeonejune/work/SummaryNode/out1.html");
        hp.readParseData();
        hp.htmlParseData();*/

        System.out.println("end!!");
    }
}
