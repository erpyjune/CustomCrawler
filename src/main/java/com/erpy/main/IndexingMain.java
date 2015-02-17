package com.erpy.main;

import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by baeonejune on 14. 12. 30..
 */
public class IndexingMain {

    private static Logger logger = Logger.getLogger(IndexingMain.class.getName());
    private static final String IndexingUrl = "http://localhost:9200/shop/";

    public static void main (String args[]) throws IOException {
        Map<String,String> statusParamMap = new HashMap<String, String>();
        SearchData searchData = new SearchData();
        SearchDataService searchDataService = new SearchDataService();
        OkMallProc okMallProc = new OkMallProc();
        int indexCount=0;

        // select 문 파라메터로 보낼 변수들
        statusParamMap.put("selStatus1","U");
        statusParamMap.put("selStatus2","I");

        //List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        List<SearchData> searchDataList = searchDataService.getAllSearchDataForUpdate(statusParamMap);
        Iterator iterator = searchDataList.iterator();

        while (true) {
            if (!(iterator.hasNext())) break;
            searchData = (SearchData)iterator.next();
//            System.out.println(String.format("(%d)%s",searchData.getDataId(), searchData.getCpName()));
            if (searchData.getCpName()==null) continue;
            if (searchData.getCpName().equals(GlobalInfo.CP_OKMALL)) {
                // indexing to elasticsearch engine.
                okMallProc.indexingOkMall(searchData);

                // update 'I' or 'U' --> 'E'
                searchData.setDataStatus("E");
                searchDataService.updateSearchDataStatus(searchData);
                indexCount++;
            } else {
                System.out.println("cp name is not equals !!");
            }
        }
        logger.info("=====================================");
        logger.info("=====================================");
        logger.info(String.format("[ %d ] indexing completed!!",indexCount));
    }
}
