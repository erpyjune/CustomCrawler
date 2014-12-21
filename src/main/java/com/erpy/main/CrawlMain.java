package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
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
        seedService = new SeedService();

        // savePath prefix.
        String savePrefixPath = globalInfo.getSaveFilePath();

        crawlSite.setConnectionTimeout(1000);
        crawlSite.setSocketTimeout(1000);
        crawlSite.setCrawlEncode("UTF-8");

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
            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(),crawlSavePath);
            crawlIO.executeSaveData();

            System.out.println("Crawling completed!!");
        }

    }
}
