package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.parser.ParseOkMall;

import java.io.IOException;
import java.util.*;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ExtractDataMain {

    private static CrawlDataService crawlDataService;

    public static void main(String args[]) throws IOException {
        CrawlData crawlData;
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();

        // okmall.
        ParseOkMall parseOkMall = new ParseOkMall();

        crawlDataService = new CrawlDataService();
        List<CrawlData> crawlDataList = crawlDataService.getCrawlDataByCpName("okmall");
        Iterator iterator = crawlDataList.iterator();
        while (iterator.hasNext()) {
            crawlData = (CrawlData)iterator.next();
//            System.out.println(crawlData.getCpName());
//            System.out.println(crawlData.getCrawlDate());
//            System.out.println(crawlData.getSavePath());

            // okmall parser.
            parseOkMall.setFilePath(crawlData.getSavePath());
            parseOkMall.extractOkMall();

            System.out.println("----------------------");
        }
        System.out.println("end!!");
    }
}
