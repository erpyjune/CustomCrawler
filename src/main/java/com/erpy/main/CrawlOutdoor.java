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
 * Created by baeonejune on 15. 5. 4..
 */
public class CrawlOutdoor {
    private static Logger logger = Logger.getLogger("CrawlOutdoor");

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
//            logger.info(String.format(" All Crawling DB Key(%s)", crawlData.getHashMD5() + crawlData.getCpName()));
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

//        CrawlMainThread crawlMainThread = new CrawlMainThread();

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

            if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.okmall.com", "http://www.okmall.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.brand_detail_layer p.item_title a span.prName_PrName");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_FIRST)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.chocammall.co.kr", "http://www.chocammall.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page_no");
                crawlMainThread.setExtractDataCount(30);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div.list_01 span.sub_img");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.campingmall.co.kr", "http://www.campingmall.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                // header setting은 crawlData 안에서 한다.
//                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("sbclub.co.kr", "http://sbclub.co.kr");
//                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("p.title");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                SB sb = new SB();
                sb.crawlData(strUrl, strKeyword, strCpName, allCrawlDatas);
            }  else if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.dicamping.co.kr", "http://www.dicamping.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("dl.item-list2");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.ccamping.co.kr", "http://www.ccamping.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style*=\"padding:0 0 3px 0px; color:#315ed2; \"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CAMPINGON)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.campingon.co.kr", "http://www.campingon.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("td[width=\"20%\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CampTown)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("camptown.firstmall.kr", "http://camptown.firstmall.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span[style=\"color:#333333;font-family:dotum;font-size:10pt;font-weight:normal;text-decoration:none;\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Aldebaran)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.adbr.co.kr", "http://www.adbr.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("li[class=\"item xans-record-\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_OMyCamping)) { // 사이트 개편되었음.
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.o-mycamping.com", "http://www.o-mycamping.com/");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style=\"padding:3\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CampI)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.campi.kr", "http://www.campi.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("td[width=130] a font[STYLE=\"color:#555555;font-size:12px;font-style:normal;font-weight:normal\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Camping365)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.camping365.co.kr", "http://www.camping365.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style=\"padding:5px 0;\"] a[style=\"font-family:Tahoma, Geneva, sans-serif; font-size:12px; color:#333;\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_LeisureMan)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.leisureman.co.kr", "http://www.leisureman.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span[style=\"font-size:13px;color:#555555;font-weight:bold;\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_WeekEnders)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.weekenders.co.kr", "http://www.weekenders.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("a[class=\"name\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CampingPlus)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.camping-plus.co.kr", "http://www.camping-plus.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("ps_page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style=\"padding-top:8px;text-align:center\"]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Starus)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.dkmountain.com", "http://www.dkmountain.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("span.font_red_b_14px");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CampSchule)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.campschule.co.kr", "http://www.campschule.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("td[width=120] a font");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_TongOutdoor)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("tongoutdoor.com", "http://tongoutdoor.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style*=\"padding:5\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_AirMT)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.airmt.net", "http://www.airmt.net");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style*=\"padding:5\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Gogo337)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.gogo337.co.kr", "http://www.gogo337.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("font.brandbrandname");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Totooutdoor)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.totooutdoor.com", "http://www.totooutdoor.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("utf-8");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("li.best_t1 a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_Niio)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.niio.co.kr", "http://http://www.niio.co.kr");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("input[name=p_name]");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_YahoCamping)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.yahocamping.com", "http://www.yahocamping.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("font.brandbrandname");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            }  else if (strCpName.equals(GlobalInfo.CP_CampingAmigo)) {
                CrawlMainThread crawlMainThread = new CrawlMainThread();
                HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.campingamigo.com", "http://http://www.campingamigo.com");
                crawlMainThread.setHttpRequestHeader(httpRequestHeader.getHttpRequestHeader());
                crawlMainThread.setPageType("page");
                crawlMainThread.setExtractDataCount(0);
                crawlMainThread.setCrawlEncode("euc-kr");
                crawlMainThread.setSaveEncode("utf-8");
                crawlMainThread.setContentExtractCountPattern("div[style*=\"padding:5\"] a");
                crawlMainThread.setCrawlUrl(strUrl);
                crawlMainThread.setUrlKeyword(strKeyword);
                crawlMainThread.setCpName(strCpName);
                crawlMainThread.setExtractType("html");
                crawlMainThread.setAllCrawlDatas(allCrawlDatas);
                crawlMainThread.run();
            } else {
                logger.error(String.format(" Other cp exist - (%s)", strCpName));
            }
        }

        logger.info(" End crawling !!");
    }
}
