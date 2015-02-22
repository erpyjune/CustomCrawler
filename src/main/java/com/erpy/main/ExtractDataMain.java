package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.*;


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
        Map<String, SearchData> newSearchDataMap;

        ///////////////////////////////////////////////////////////////////
        // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
        Map<String, SearchData> allSearchDatasMap = new HashMap<String, SearchData>();
        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator searchDataListIt = searchDataList.iterator();
        SearchData sd;
        int existCount=0;

        while (searchDataListIt.hasNext()) {
            sd = (SearchData) searchDataListIt.next();
            if (allSearchDatasMap.containsKey(sd.getProductId())) {
                existCount++;
            }
//            logger.debug(String.format(" product_id(%s)(%s)", sd.getProductId(), sd.getContentUrl()));
            allSearchDatasMap.put(sd.getProductId(), sd);
        }

//        logger.debug(String.format(" 기존 모든 데이터 크기 - Total(%d), Exist(%d)", allSearchDatasMap.size(), existCount));

        // crawling한 원본 데이터를 db에서 하나씩 가져온다.
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        List<CrawlData> crawlDataList = crawlDataService.getAllCrawlDatas();
        Iterator iterator = crawlDataList.iterator();
        while (iterator.hasNext()) {

            crawlData = (CrawlData) iterator.next();

            // okmall processing.
            if (crawlData.getCpName().equals(GlobalInfo.CP_OKMALL)) {

                // set parsing file path.
                okMallProc.setFilePath(crawlData.getSavePath());

                // set keyword.
                okMallProc.setKeyword(crawlData.getCrawlKeyword());

                // extract data.
                logger.info(String.format(" 데이터 추출할 파일 - %s", crawlData.getSavePath()));
                searchDataMap = okMallProc.extractOkMall();
                // 추출된 데이터가 없음당.
                if (searchDataMap.size() <= 0) {
                    logger.error(String.format(" 이 파일은 추출된 데이터가 없습니다 (%s)",crawlData.getSavePath()));
                    continue;
                }

                // DB에 들어있는 데이터와 쇼핑몰에서 가져온 데이터를 비교한다.
                // 비교결과 update, insert할 데이터를 모아서 리턴 한다.
                newSearchDataMap = okMallProc.checkSearchDataValid(allSearchDatasMap, searchDataMap);
                if (newSearchDataMap.size() <= 0) {
                    // skip count 계산.
                    int count = okMallProc.getSkipCount();
                    int mergeCount = count + searchDataMap.size();
                    okMallProc.setSkipCount(mergeCount);
                    logger.info(String.format(" 변경되거나 새로 생성된 상품 데이터가 없습니다 - %s", crawlData.getSavePath()));
                }
                else {

                    ////////////////////////////////////////
                    // insert or update.
                    okMallProc.insertOkMall(newSearchDataMap);


                    // insert 되거나 update된 데이터들을 다시 allSearchDataMap에 입력하여
                    // 새로 parsing되서 체크하는 데이터 비교에 반영될 수 있도록 한다.
                    String productId;
                    SearchData tmpSD;
                    for(Map.Entry<String, SearchData> entry : newSearchDataMap.entrySet()) {
                        productId = entry.getKey().trim();
                        tmpSD = entry.getValue();
                        // insert..
                        allSearchDatasMap.put(productId, tmpSD);
                    }
                    newSearchDataMap.clear();
                }
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_FIRST)) {
                logger.warn(" first cp processing");
            } else {
                logger.warn(" other cp occurred!!");
            }
        }

        logger.info(" ================== Extracting information ==================");
        logger.info(String.format(" Total extract count - %d", okMallProc.getTotalExtractCount()));
        logger.info(String.format(" Skip count          - %d", okMallProc.getSkipCount()));
        logger.info(String.format(" Insert count        - %d", okMallProc.getInsertCount()));
        logger.info(String.format(" Update count        - %d", okMallProc.getUpdateCount()));
        logger.info(String.format(" Unknoun count       - %d", okMallProc.getUnknownCount()));
        logger.info(" Extract processing terminated normally!!");

    }
}
