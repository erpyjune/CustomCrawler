package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import org.apache.commons.lang3.StringUtils;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
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
        String strCpName;

        OkMallProc okMallProc = new OkMallProc();
        seedService = new SeedService();
        crawlDataService = new CrawlDataService();

        // get crawl seeds.
        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            strKeyword = seed.getKeyword();
            strUrl     = seed.getUrl();
            strCpName  = StringUtils.trim(seed.getCpName());

            System.out.println("Url... " + strUrl);

            if (strCpName.equals("okmall")==true) {
                okMallProc.setTxtEncode("euc-kr");
                okMallProc.crawlData(strUrl, strKeyword, strCpName);
            }
        }

        System.out.println("Crawling completed!!");
    }
}
