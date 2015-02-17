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
import org.elasticsearch.search.aggregations.bucket.global.Global;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlMain {

    private static Logger logger = Logger.getLogger(CrawlMain.class.getName());

    public static void main(String args[]) throws IOException {

        Seed seed;
        String strKeyword;
        String strUrl;
        String strCpName;
        int seedCount=0;

        OkMallProc okMallProc = new OkMallProc();
        SeedService seedService = new SeedService();

        // all crawl_data delete.
        CrawlDataService crawlDataService = new CrawlDataService();
        crawlDataService.deleteCrawlDataAll();
        logger.info("crawl_data table delete all");

        // get crawl seeds.
        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            strKeyword = seed.getKeyword();
            strUrl     = seed.getUrl();
            strCpName  = StringUtils.trim(seed.getCpName());

            logger.info(String.format("[ %d ] seed crawling...", seedCount));

            if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                okMallProc.setTxtEncode("euc-kr");
                okMallProc.crawlData(strUrl, strKeyword, strCpName);
            }

            seedCount++;
        }

        logger.info("crawling completed!!");
    }
}
