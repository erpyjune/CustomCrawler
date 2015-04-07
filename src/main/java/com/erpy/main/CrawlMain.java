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
        int seedCount = 0;


        CrawlIO crawlIO = new CrawlIO();

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

            if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("td[width=\"25%\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_FIRST)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_CAMPINGON)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_CampTown)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("dl.item-list2");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_Aldebaran)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("utf-8");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("li[class=\"item xans-record-\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_OMyCamping)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("utf-8");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("div[style=\"padding:3\"] a");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
//             page & offset
            if (strCpName.equals(GlobalInfo.CP_CampI)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("td[width=130] a font[STYLE=\"color:#555555;font-size:12px;font-style:normal;font-weight:normal\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_Camping365)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("div[style=\"padding:5px 0;\"] a[style=\"font-family:Tahoma, Geneva, sans-serif; font-size:12px; color:#333;\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_LeisureMan)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("span[style=\"font-size:13px;color:#555555;font-weight:bold;\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_WeekEnders)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("a[class=\"name\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }
            if (strCpName.equals(GlobalInfo.CP_CampingPlus)) {
                crawlIO.setPageType("page");
                crawlIO.setExtractDataCount(0);
                crawlIO.setCrawlEncoding("euc-kr");
                crawlIO.setSaveEncoding("utf-8");
                crawlIO.setPattern("div[style=\"padding-top:8px;text-align:center\"]");
                crawlIO.crawling(strUrl, strKeyword, strCpName, allCrawlDatas);
            }



//            if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
//                CCamping cp = new CCamping();
//                cp.crawlData(strUrl, strKeyword, strCpName, allCrawlDatas);
//            } else if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
//                DICamping cp = new DICamping();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } aaaelse if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
//                SB cp = new SB();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
//                CampingMall cp = new CampingMall();
//                cp.setTxtEncode("euc-kr");
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
//                OkMallProc okMallProc = new OkMallProc();
//                okMallProc.setTxtEncode("euc-kr");
//                okMallProc.crawlData(strUrl, strKeyword, strCpName);
//            } aaaelse if (strCpName.equals(GlobalInfo.CP_FIRST)) {
//                First first = new First();
//                first.setTxtEncode("utf-8");
//                first.crawlData(strUrl, strKeyword, strCpName);
////            } else if (strCpName.equals(GlobalInfo.CP_CAMPINGON)) {
////                CampingOn cp = new CampingOn();
////                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_CampTown)) {
//                CampTown cp = new CampTown();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_Aldebaran)) {
//                Aldebaran cp = new Aldebaran();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_OMyCamping)) {
//                OMyCamping omy = new OMyCamping();
//                omy.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_CampI)) {
//                CampI cmpi = new CampI();
//                cmpi.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_Camping365)) {
//                Camping365 cp365 = new Camping365();
//                cp365.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_LeisureMan)) {
//                LeisureMan lsm = new LeisureMan();
//                lsm.crawlData(strUrl, strKeyword, strCpName);
//            }
//            if (strCpName.equals(GlobalInfo.CP_WeekEnders)) {
//                WeekEnders wk = new WeekEnders();
//                wk.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_CampingPlus)) {
//                CampingPlus cplus = new CampingPlus();
//                cplus.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_SnowPeak)) {
//                SnowPeak sp = new SnowPeak();
//                sp.crawlData(strUrl, strKeyword, strCpName);
//            }
        }

        logger.info(" End crawling !!");
    }
}
