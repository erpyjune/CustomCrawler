package com.erpy.main;

import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.dao.ThumbnailData;
import com.erpy.dao.ThumbnailDataService;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 15. 6. 14..
 */
public class IndexingSingle {
    private static Logger logger = Logger.getLogger(IndexingSingle.class.getName());
    private static final String IndexingUrl = "http://summarynode.cafe24.com:9200/shop/";

    public static void main(String args[]) throws Exception {
        Map<String, String> statusParamMap = new HashMap<String, String>();
        SearchData searchData;
        SearchDataService searchDataService = new SearchDataService();
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnail;
        GlobalUtils globalUtils = new GlobalUtils();
        String cpName = "";
        String contentUrl = "";
        String indexStatus = "";
        int indexCount = 0;
        int returnCode=0;


        if (args.length > 0) {
            indexStatus = args[0];
            cpName = args[1];
            contentUrl = args[2];
        }

        // select 문 파라메터로 보낼 변수들
        if (indexStatus.length() > 0) {
            statusParamMap.put("selStatus1", indexStatus);
            statusParamMap.put("selStatus2", "I");
        } else {
//            statusParamMap.put("selStatus1","E");
            statusParamMap.put("selStatus1", "U");
            statusParamMap.put("selStatus2", "I");
        }


        //List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        List<SearchData> searchDataList = searchDataService.getAllSearchDataForUpdate(statusParamMap);
        Iterator iterator = searchDataList.iterator();
        while (true) {
            if (!(iterator.hasNext())) break;
            searchData = (SearchData) iterator.next();
            if (cpName.length() > 0 && !cpName.equals("all")) {
                if (!cpName.equals(searchData.getCpName())) continue;
            }

            if (!contentUrl.equals(searchData.getContentUrl())) continue;

            if (globalUtils.isDataEmpty(searchData)) {
                logger.error(" Skip indexing :: data field is null !!");
                continue;
            }

            // get thumbnail
            thumbnailData.setCpName(searchData.getCpName());
            thumbnailData.setProductId(searchData.getProductId());
            dbThumbnail = thumbnailDataService.getFindThumbnailData(thumbnailData);
            if (dbThumbnail==null || dbThumbnail.getBigThumbUrl().length()<=0) {
                logger.error(String.format(" Not exist big thumbnail (%s) (%s)",
                        searchData.getCpName(), searchData.getProductId()));
                continue;
            }

            try {
                returnCode = globalUtils.indexingES(searchData, dbThumbnail);
                if (returnCode == 200 || returnCode == 201) {
                    // update 'I' or 'U' --> 'E'
                    searchData.setDataStatus("E");
                    searchDataService.updateSearchDataStatus(searchData);
                    indexCount++;
                }
            }
            catch (Exception e) {
                logger.error(String.format(" Indexing error - return(%d),dataId(%d) | %s",
                        returnCode, searchData.getDataId(), e.getStackTrace().toString()));
            }
        }
        logger.info("======================================================");
        logger.info(String.format(" Index count %d completed!!", indexCount));
    }
}
