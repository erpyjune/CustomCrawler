package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.First;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ExtractDataMain {

    private static Logger logger = Logger.getLogger(ExtractDataMain.class.getName());

    ///////////////////////////////////////////////////////////////////
    // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
    // key = cpName + productId
    ///////////////////////////////////////////////////////////////////
    private static Map<String, SearchData> getAllProductKey() throws Exception {
        Map<String, SearchData> allSearchDatasMap = new HashMap<String, SearchData>();
        SearchDataService searchDataService = new SearchDataService();

        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator searchDataListIt = searchDataList.iterator();
        SearchData sd;
        int existCount=0;

        while (searchDataListIt.hasNext()) {
            sd = (SearchData) searchDataListIt.next();
            if (allSearchDatasMap.containsKey(sd.getProductId())) {
                existCount++;
            }
            logger.debug(String.format(" All DB Set Key(%s%s)", sd.getProductId(), sd.getCpName()));
            allSearchDatasMap.put(sd.getProductId() + sd.getCpName(), sd);
        }
        logger.debug(String.format(" 기존 모든 데이터 크기 - Total(%d), Exist(%d)", allSearchDatasMap.size(), existCount));
        return allSearchDatasMap;
    }

    public static void main(String args[]) throws Exception {
        CrawlData crawlData;
        CrawlDataService crawlDataService = new CrawlDataService();

        First first = new First();
        OkMallProc okMallProc = new OkMallProc();

        // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
        Map<String, SearchData> allSearchDatasMap = getAllProductKey();

        // crawling한 원본 데이터를 db에서 하나씩 가져온다.
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        List<CrawlData> crawlDataList = crawlDataService.getAllCrawlDatas();
        Iterator iterator = crawlDataList.iterator();
        while (iterator.hasNext()) {

            crawlData = (CrawlData) iterator.next();

            if (crawlData.getCpName().equals(GlobalInfo.CP_OKMALL)) {
                okMallProc.mainExtractProcessing(okMallProc, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_FIRST)) {
                first.mainExtractProcessing(first,crawlData,allSearchDatasMap);
            } else {
                logger.warn(" other cp occurred!!");
            }
        }

        okMallProc.printResultInfo(okMallProc);
        first.printResultInfo(first);
    }
}
