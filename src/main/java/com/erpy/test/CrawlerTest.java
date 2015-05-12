package com.erpy.test;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.utils.GlobalInfo;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class CrawlerTest
{
    public static void main( String[] args ) throws  Exception {
        String log=null;
        String crawlUrl=null;
        String encodeType=null;

        String site1 = "http://www.okmall.com/product/product.html?pID=20000677&UNI=M";

        System.out.println("args length : " + args.length);

        if (args.length != 2) {
            System.out.println("(needs) crawl-url encode-type");
            System.exit(0);
        }

        for (int i=0; i<args.length; i++) {
            log = String.format("args[%d] : %s", i, args[i]);
            System.out.println(log);
        }

        crawlUrl = args[0];
        encodeType = args[1];

        CrawlSite cs = new CrawlSite();
        CrawlIO ci = new CrawlIO();
        GlobalInfo globalInfo = new GlobalInfo();

        try {
            // set crawl info.
            cs.setCrawlUrl(site1);
            cs.setCrawlEncode(encodeType);

            cs.setConnectionTimeout(1000);
            cs.setSocketTimeout(1000);

            String crawlSavePath = globalInfo.getSaveFilePath();

            //cs.HttpCrawlGetMethod1();
            cs.HttpCrawlGetDataTimeout();
            ci.setSaveDataInfo(cs.getCrawlData(), crawlSavePath + "/" + "test.html", "euc-kr");
            ci.executeSaveData();
            System.out.println("GET end!!");

            //cs.setCrawlUrl(site1);
            //cs.HttpCrawlGetMethod2();
            //ci.setSaveDataInfo(cs.getCrawlData(), path2);
            //ci.executeSaveData();

            /* post method
            cs.setCrawlUrl(site2);
            cs.setCrawlEncode("UTF-8");
            cs.HttpCrawlPostMethod();
            ci.setSaveDataInfo(cs.getCrawlData(), path2);
            ci.executeSaveData();
            System.out.println("post end!!");
            */

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("end!!");
        }
    }
}
