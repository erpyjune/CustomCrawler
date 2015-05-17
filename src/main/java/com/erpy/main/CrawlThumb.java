package com.erpy.main;

import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by baeonejune on 15. 5. 17..
 */
public class CrawlThumb {
    private static Logger logger = Logger.getLogger("CrawlThumb");

    ///////////////////////////////////////////////////////////////////
    // 모든 search 데이터를 내린다.
    private static Map<String, SearchData> getAllSearchDatas(String cpName) throws Exception {
        Map<String, SearchData> allSearchDataMap = new HashMap<String, SearchData>();
        SearchDataService searchDataService = new SearchDataService();
        SearchData searchData;
        int existCount=0;

//        List<SearchData> searchDatas = searchDataService.getAllSearchDatas();
        List<SearchData> searchDatas = searchDataService.getSearchDataByCpName(cpName);
        Iterator searchDataIterator = searchDatas.iterator();
        while (searchDataIterator.hasNext()) {
            searchData = (SearchData) searchDataIterator.next();
            if (allSearchDataMap.containsKey(searchData.getProductId())) {
                existCount++;
            }
//            logger.info(String.format(" All Crawling DB Key(%s)", crawlData.getHashMD5() + crawlData.getCpName()));
            allSearchDataMap.put(searchData.getProductId(), searchData);
        }
        logger.info(String.format(" 기존 모든 데이터 크기 - Total(%d), Exist(%d)", allSearchDataMap.size(), existCount));
        return allSearchDataMap;
    }

    public static void main(String[] args) throws Exception {
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        SearchData searchData;
        String strItem;
        String key, imageFileName;
//        String localPath = "/Users/baeonejune/work/SummaryNode/images/airmt";
        String localPath = "/Users/baeonejune/work/SummaryNode/images/tongoutdoor";
//        String prefixHostThumbUrl="http://www.airmt.net";
        String prefixHostThumbUrl="http://tongoutdoor.com";

        ////////////////////////////////////////////////////////////////////////
        Map<String, SearchData> searchDataMap = getAllSearchDatas("tongoutdoor");
        SearchDataService searchDataService = new SearchDataService();
        for(Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

//            logger.info("id       : " + searchData.getProductId());
//            logger.info("cp_name  : " + searchData.getCpName());

            // 환경 셋팅
//            HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.airmt.net", "http://www.airmt.net");
            HttpRequestHeader httpRequestHeader = new HttpRequestHeader("tongoutdoor.com", "http://tongoutdoor.com");
            crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
            crawlSite.setConnectionTimeout(5000);
            crawlSite.setSocketTimeout(10000);
            crawlSite.setCrawlEncode("euc-kr");
            crawlSite.setCrawlUrl(searchData.getContentUrl());

            int returnCode;
            int crawlErrorCount=0;
            for (;;) {
                try {
                    returnCode = crawlSite.HttpCrawlGetDataTimeout();
                    if (returnCode != 200 && returnCode != 201) {
                        logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (crawlErrorCount >= 3) break;
                    crawlErrorCount++;
                    logger.error(Arrays.toString(e.getStackTrace()));
                }
            }

            doc = Jsoup.parse(crawlSite.getCrawlData());
            elements = doc.select("div[style=\"padding-bottom:10\"]");
            for (Element element : elements) {
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("span img");
                for (Element et : listE) {
                    strItem = et.attr("src");
                    if (strItem.contains("../data/goods")) {
                        searchData.setThumbUrlBig(prefixHostThumbUrl + strItem.replace("../data", "/shop/data"));
//                        logger.info(prefixHostThumbUrl + strItem.replace("../data", "/shop/data"));
                        break;
                    } else {
                        searchData.setThumbUrlBig(prefixHostThumbUrl + strItem);
//                        logger.info(prefixHostThumbUrl + strItem);
                    }
                }
            }


            int imageSaveErrorCount=0;

            while(true) {
                try {
                    imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                    globalUtils.saveDiskImgage(localPath, searchData.getThumbUrlBig(), imageFileName);
                    searchDataService.updateSearchData(searchData);
                    logger.info(String.format(" update (%s)", searchData.getThumbUrlBig()));
                    break;
                } catch (Exception e) {
                    if (imageSaveErrorCount > 3) break;
                    logger.error(String.format(" Download image (%s) faile (%s)",
                            searchData.getThumbUrlBig(), e.getStackTrace().toString()));
                    imageSaveErrorCount++;
                }
            }
        }
    }
}
