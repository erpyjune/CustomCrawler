package com.erpy.main;

import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


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
        logger.info(" crawl_data table delete all");

        // get crawl seeds.
        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            strKeyword = seed.getKeyword();
            strUrl     = seed.getUrl();
            strCpName  = StringUtils.trim(seed.getCpName());

            if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                okMallProc.setTxtEncode("euc-kr");
                // 데이터 수집 시작..
                okMallProc.crawlData(strUrl, strKeyword, strCpName);
            }
        }

        logger.info(" ================== Crawling information ==================");
        logger.info(String.format(" Total crawling count - %d", okMallProc.getCrawlCount()));
        logger.info(String.format(" Error crawling count - %d", okMallProc.getCrawlErrorCount()));
        logger.info(String.format(" Collision file count - %d", okMallProc.getCollisionFileCount()));
    }
}
