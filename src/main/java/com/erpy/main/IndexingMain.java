package com.erpy.main;

import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by baeonejune on 14. 12. 30..
 */
public class IndexingMain {

    private static Logger logger = Logger.getLogger(IndexingMain.class.getName());
    private static final String IndexingUrl = "http://localhost:9200/shop/";

    public static void main (String args[]) throws IOException {
        Map<String,String> statusParamMap = new HashMap<String, String>();
        SearchData searchData;
        SearchDataService searchDataService = new SearchDataService();
        OkMallProc okMallProc = new OkMallProc();
        String productId;
        String cpName;
        int indexCount=0;
        int returnCode;

        // select 문 파라메터로 보낼 변수들
//        statusParamMap.put("selStatus1","U");
//        statusParamMap.put("selStatus2","I");
        statusParamMap.put("selStatus1","E");
        statusParamMap.put("selStatus2","I");

        //List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        List<SearchData> searchDataList = searchDataService.getAllSearchDataForUpdate(statusParamMap);
        Iterator iterator = searchDataList.iterator();
        while (true) {
            if (!(iterator.hasNext())) break;
            searchData = (SearchData)iterator.next();

//            logger.info(String.format(" (%s)%s:%s", searchData.getProductId(), searchData.getCpName(),
//                    searchData.getProductName()));

            if (okMallProc.isDataEmpty(searchData)) {
                logger.error(" Skip indexing :: data field is null !!");
                continue;
            }

            if (searchData.getCpName().equals(GlobalInfo.CP_OKMALL) ||
                    searchData.getCpName().equals(GlobalInfo.CP_CCAMPING) ||
                    searchData.getCpName().equals(GlobalInfo.CP_DICAMPING) ||
                    searchData.getCpName().equals(GlobalInfo.CP_SBCLUB) ||
                    searchData.getCpName().equals(GlobalInfo.CP_CAMPINGMALL) ||
                    searchData.getCpName().equals(GlobalInfo.CP_CAMPINGON) ||
                    searchData.getCpName().equals(GlobalInfo.CP_CampTown) ||
                    searchData.getCpName().equals(GlobalInfo.CP_Aldebaran) ||
                    searchData.getCpName().equals(GlobalInfo.CP_OMyCamping) ||
                    searchData.getCpName().equals(GlobalInfo.CP_FIRST)) {

                // indexing to elasticsearch engine.
                returnCode = okMallProc.indexingOkMall(searchData);
                if (returnCode == 200 || returnCode == 201) {
                    // update 'I' or 'U' --> 'E'
                    searchData.setDataStatus("E");
                    searchDataService.updateSearchDataStatus(searchData);
                    indexCount++;
                }
            } else {
                logger.error(" Skip cp name is - " + searchData.getCpName());
            }
        }

        logger.info("=====================================");
        logger.info(String.format(" Index count %d completed!!", indexCount));
    }
}
