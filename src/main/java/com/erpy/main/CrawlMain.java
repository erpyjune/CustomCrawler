package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.*;
import com.erpy.parser.*;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlMain {
    private static Logger logger = Logger.getLogger(CrawlMain.class.getName());
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
            if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style*=\"padding:0 0 3px 0px; color:#315ed2; \"]");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("dl.item-list2");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.brand_detail_layer p.item_title a span.prName_PrName");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_G9)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("deals");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_GSDeal)) {
                crawlMainThread.setPageType("pageIdx");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("list");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_LotteThanksDeal)) {
                crawlMainThread.setPageType("pageIdx");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("p.stitle");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            }   else if (strCpName.equals(GlobalInfo.CP_HotKill)) {
                crawlMainThread.setPageType("");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("goodsList");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("json");
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_Timon)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.info p.tit");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_WeMef)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.info p.dt");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            } else if (strCpName.equals(GlobalInfo.CP_CouPang)) {
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span.plp-square-img");
                crawlMainThread.setSeed(seed);
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.setExtractType("html");
                crawlMainThread.run();
            }



//            ===============================================================================
//            ===============================================================================
            if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "div[style*=\"padding:0 0 3px 0px; color:#315ed2; \"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "dl.item-list2");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
                SB sb = new SB();
//                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "p.title");
                sb.crawlData(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "div.brand_detail_layer p.item_title a span.prName_PrName");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_FIRST)) { // complete
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page_no", 30, "utf-8", "utf-8", "div.list_01 span.sub_img");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_CAMPINGON)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "td[width=\"20%\"] a");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_CampTown)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "utf-8", "utf-8", "span[style=\"color:#333333;font-family:dotum;font-size:10pt;font-weight:normal;text-decoration:none;\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_Aldebaran)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "utf-8", "utf-8", "li[class=\"item xans-record-\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_OMyCamping)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "div[style=\"padding:3\"] a");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_CampI)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "td[width=130] a font[STYLE=\"color:#555555;font-size:12px;font-style:normal;font-weight:normal\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_Camping365)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "div[style=\"padding:5px 0;\"] a[style=\"font-family:Tahoma, Geneva, sans-serif; font-size:12px; color:#333;\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_LeisureMan)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "span[style=\"font-size:13px;color:#555555;font-weight:bold;\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_WeekEnders)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("page", 0, "euc-kr", "utf-8", "a[class=\"name\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
            else if (strCpName.equals(GlobalInfo.CP_CampingPlus)) {
                CrawlIO crawlIO = new CrawlIO();
                crawlIO.setCrawlIO("ps_page", 0, "euc-kr", "utf-8", "div[style=\"padding-top:8px;text-align:center\"]");
                crawlIO.crawl(seed, allCrawlDatas);
            }
//            else if (strCpName.equals(GlobalInfo.CP_CooPang)) {
//                CrawlIO crawlIO = new CrawlIO();
//                crawlIO.setCrawlIO("page", 0, "utf-8", "utf-8", "span.plp-square-img");
//                crawlIO.crawl(seed, allCrawlDatas);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_WeMef)) {
//                CrawlIO crawlIO = new CrawlIO();
//                crawlIO.setCrawlIO("page", 0, "utf-8", "utf-8", "div.info p.dt");
//                crawlIO.crawlOne(strUrl, strKeyword, strCpName, allCrawlDatas);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_Timon)) {
//                CrawlIO crawlIO = new CrawlIO();
//                crawlIO.setCrawlIO("page", 0, "utf-8", "utf-8", "div.info p.tit");
//                crawlIO.crawlOne(strUrl, strKeyword, strCpName, allCrawlDatas);
//                crawlIO.crawlTimon(strUrl, strKeyword, strCpName, allCrawlDatas);
            else {
                logger.error(String.format(" Other cp exist - (%s)", strCpName));
            }
        }

        logger.info(" End crawling !!");
    }
}
