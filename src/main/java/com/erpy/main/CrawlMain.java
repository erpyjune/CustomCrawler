package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlMain {

    private static SeedService seedService;
    private static CrawlDataService crawlDataService;

    public static void main(String args[]) throws IOException {

        Seed seed;
        String strKeyword;
        String strUrl;
        String crawlSavePath;
        int randomNum;

        CrawlSite crawlSite = new CrawlSite();
        Random random = new Random();
        CrawlIO crawlIO = new CrawlIO();
        GlobalInfo globalInfo = new GlobalInfo();
        DateInfo dateInfo = new DateInfo();
        seedService = new SeedService();

        CrawlData crawlData = new CrawlData();
        crawlDataService = new CrawlDataService();


        // savePath prefix.
        String savePrefixPath = globalInfo.getSaveFilePath();

        crawlSite.setConnectionTimeout(1000);
        crawlSite.setSocketTimeout(1000);
        crawlSite.setCrawlEncode("euc-kr");

        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            strKeyword = seed.getKeyword();
            strUrl     = seed.getUrl();

            System.out.println("Crawling... " + strKeyword);
            System.out.println("Url... " + strUrl);

            // crawling.
            crawlSite.setCrawlUrl(strUrl);
            crawlSite.HttpCrawlGetDataTimeout();

            // save file.
            randomNum = random.nextInt(9182773);
            crawlSavePath = savePrefixPath + "/" + Integer.toString(randomNum) + ".html";
            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, "euc-kr");
            crawlIO.executeSaveData();

            // insert save file info.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlDataService.insertCrawlData(crawlData);

        }

        System.out.println("Crawling completed!!");
    }
}
