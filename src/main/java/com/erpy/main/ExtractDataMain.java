package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.parser.OkMallProc;

import java.io.IOException;
import java.util.*;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ExtractDataMain {

    private static CrawlDataService crawlDataService;

    public static void main(String args[]) throws IOException {
        CrawlData crawlData;
        // okmall.
        OkMallProc okMallProc = new OkMallProc();
        List<SearchData> searchDataList = new ArrayList<SearchData>();

        // okmall process.
        crawlDataService = new CrawlDataService();
        List<CrawlData> crawlDataList = crawlDataService.getAllCrawlDatas();
        Iterator iterator = crawlDataList.iterator();
        while (iterator.hasNext()) {
            crawlData = (CrawlData)iterator.next();

            if (crawlData.getCpName().equals("okmall")==true) {
                // set parsing file path.
                System.out.println("extract - " + crawlData.getSavePath());
                okMallProc.setFilePath(crawlData.getSavePath());
                // set keyword.
                okMallProc.setKeyword(crawlData.getCrawlKeyword());
                // parsing.
                searchDataList = okMallProc.extractOkMall();
                // insert to DB.
                okMallProc.insertOkMall(searchDataList);
            }
        }

        System.out.println("=========================");
        System.out.println("Extract processing end !!");
    }
}
