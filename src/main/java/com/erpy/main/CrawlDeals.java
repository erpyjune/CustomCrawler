package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.HttpRequestHeader;
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
 * Created by baeonejune on 15. 4. 19..
 */
public class CrawlDeals {
    private static Logger logger = Logger.getLogger(CrawlDeals.class.getName());
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
        String strKeyword;
        String strUrl;
        String strCpName;
        String argsCPname="";


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

            if (argsCPname.length()>0) {
                if (!seed.getCpName().equals(argsCPname)) continue;
            }

            if (seed.getCpName().equals(GlobalInfo.CP_G9)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.g9.co.kr","http://m.g9.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("deals");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_GSDeal)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.gsshop.com","http://m.gsshop.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("list");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            }  else if (seed.getCpName().equals(GlobalInfo.CP_LotteThanksDeal)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.lotteimall.com","http://m.lotteimall.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("p.stitle");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            }   else if (seed.getCpName().equals(GlobalInfo.CP_HotKill)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.hnsmall.com","http://m.hnsmall.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("goodsList");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_Timon)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.ticketmonster.co.kr","http://m.ticketmonster.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.info p.tit");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_WeMef)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.wemakeprice.com","http://m.wemakeprice.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.info p.dt");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_CouPang)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.coupang.com","http://m.coupang.com/nm/");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span.plp-square-img");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_HappyVirusFirst)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.shinsegaemall.ssg.com","http://m.shinsegaemall.ssg.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.hb_article_li ");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_HappyVirusPost)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.shinsegaemall.ssg.com","http://m.shinsegaemall.ssg.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.hb_article2_li ");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (seed.getCpName().equals(GlobalInfo.CP_HappyDeals)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.nsmall.com","http://m.nsmall.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span.price");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else {
                logger.error(String.format(" Other cp exist - (%s)", seed.getCpName()));
            }
        }

        logger.info(" End crawling !!");
    }
}
