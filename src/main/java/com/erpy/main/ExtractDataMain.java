package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ExtractDataMain {

    private static Logger logger = Logger.getLogger(ExtractDataMain.class.getName());

    public static void main(String args[]) throws Exception {
        CrawlData crawlData;
        OkMallProc okMallProc = new OkMallProc();
        CrawlDataService crawlDataService = new CrawlDataService();
        SearchDataService searchDataService = new SearchDataService();
        Map<String, SearchData> newSearchDataMap = new HashMap<String, SearchData>();

        ///////////////////////////////////////////////////////////////////
        // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.

        Map<String, SearchData> allSearchDatasMap = new HashMap<String, SearchData>();
        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator searchDataListIt = searchDataList.iterator();
        SearchData sd = new SearchData();
        if (searchDataListIt.hasNext()) {
            do {
                sd = (SearchData) searchDataListIt.next();
                allSearchDatasMap.put(sd.getContentUrl(), sd);
                //logger.info(String.format("product_name:%s", sd.getProductName()));
            } while (searchDataListIt.hasNext());
        }


        // crawling한 원본 데이터를 db에서 하나씩 가져온다.
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        List<CrawlData> crawlDataList = crawlDataService.getAllCrawlDatas();
        Iterator iterator = crawlDataList.iterator();
        if (iterator.hasNext()) {
            do {
                crawlData = (CrawlData) iterator.next();

                // okmall processing.
                if (crawlData.getCpName().equals(GlobalInfo.CP_OKMALL)) {

                    logger.info("상품 추출 : " + crawlData.getSavePath());

                    // set parsing file path.
                    okMallProc.setFilePath(crawlData.getSavePath());

                    // set keyword.
                    okMallProc.setKeyword(crawlData.getCrawlKeyword());

                    // extract data.
                    searchDataMap = okMallProc.extractOkMall();

                    // no data.
                    if (searchDataMap.size() <= 0) {
                        logger.info("추출된 상품 데이터가 없습니다.");
                        continue;
                    }

                    // DB에 들어있는 데이터와 쇼핑몰에서 가져온 데이터를 비교한다.
                    // 비교결과 update, insert할 데이터를 모아서 리턴 한다.
                    newSearchDataMap = okMallProc.checkSearchDataValid(allSearchDatasMap, searchDataMap);

//                    for(Map.Entry<String, SearchData> entry : newSearchDataMap.entrySet()) {
//                        String key = entry.getKey();
//                        SearchData sss = entry.getValue();
//
//                        logger.info("new ===================");
//                        logger.info(String.format("(%s)(%d)(%d)(%s)(%s)",
//                                sss.getType(),
//                                sss.getSalePrice(),
//                                sss.getOrgPrice(),
//                                sss.getProductName(),
//                                sss.getContentUrl()));
//                    }

                    if (newSearchDataMap.size() <= 0) {
                        logger.info("변경되거나 새로 생성된 상품 데이터가 없습니다.");
                    }
                    else {
                        // insert or update.
                        okMallProc.insertOkMall(newSearchDataMap);
                    }
                }
                else if (crawlData.getCpName().equals(GlobalInfo.CP_FIRST)) {
                    logger.warning("first cp processing");
                } else {
                    logger.warning("other cp occurred!!");
                }
            } while (iterator.hasNext());
        }

        logger.info("extract processing end normally!!");

    }
}
