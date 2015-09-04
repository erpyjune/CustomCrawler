package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.SB;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 15. 9. 4..
 */
public class CrawlCopyNpaste {
    private static Logger logger = Logger.getLogger(CrawlCopyNpaste.class.getName());
    ///////////////////////////////////////////////////////////////////
    // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
    // key = cpName + productId
    ///////////////////////////////////////////////////////////////////
    private static Map<String, CrawlData> getAllCrawlDatas() throws Exception {
        Map<String, CrawlData> allCrawlDataMap = new HashMap<String, CrawlData>();
        CrawlDataService crawlDataService = new CrawlDataService();
        CrawlData crawlData;
        int existCount=0;

        List<CrawlData> crawlDatas = crawlDataService.getAllCrawlDatas();
        Iterator crawlDataIter = crawlDatas.iterator();

        while (crawlDataIter.hasNext()) {
            crawlData = (CrawlData) crawlDataIter.next();
            if (allCrawlDataMap.containsKey(crawlData.getHashMD5())) {
                existCount++;
            }
            logger.info(String.format(" All Crawling DB Key(%s)", crawlData.getHashMD5() + crawlData.getCpName()));
            allCrawlDataMap.put(crawlData.getHashMD5() + crawlData.getCpName(), crawlData);
        }
        logger.info(String.format(" 기존 모든 데이터 크기 - Total(%d), Exist(%d)", allCrawlDataMap.size(), existCount));
        return allCrawlDataMap;
    }

    public static void main(String args[]) throws Exception {

        Seed seed;
        List<CrawlData> crawlDatas;
        String strKeyword;
        String strUrl;
        String strCpName;
        String argsCPname="";

        CrawlMainThread crawlMainThread = new CrawlMainThread();

        if (args.length > 0) {
            argsCPname = args[0];
        }

        // 기존 저장된 모든 crawl data를 loading 한다.
        Map<String, CrawlData> allCrawlDatas = getAllCrawlDatas();

        // get crawl seeds.
        SeedService seedService = new SeedService();
        List<Seed> seedList = seedService.getAllSeeds();

        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed) iterator.next();
            strKeyword = seed.getKeyword();
            strUrl = seed.getUrl();
            strCpName = seed.getCpName().trim();

            if (argsCPname.length()>0) {
                if (!strCpName.equals(argsCPname)) continue;
            }

            // thread test.
            if (strCpName.equals(GlobalInfo.CP_Danawa)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.head_info strong");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            } else {
                logger.error(String.format(" Other cp exist - (%s)", strCpName));
            }
        }

        logger.info(" End crawling !!");
    }
}
