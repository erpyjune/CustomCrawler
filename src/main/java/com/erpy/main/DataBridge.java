package com.erpy.main;

import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.dao.ThumbnailData;
import com.erpy.dao.ThumbnailDataService;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by baeonejune on 15. 7. 27..
 */
public class DataBridge {
    private static Logger logger = Logger.getLogger("DataBridge");
    public static void main(String args[]) throws Exception {
        Map<String, String> statusParamMap = new HashMap<String, String>();
        SearchData searchData;
        SearchDataService searchDataService = new SearchDataService();
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnail;
        GlobalUtils globalUtils = new GlobalUtils();
        String cpName = "";
        String indexStatus = "";
        int indexCount = 0;
        int returnCode=0;


        if (args.length==0) {
            logger.error(" USAGE: output_file_path");
            System.exit(-1);
        }

        logger.info(" Output : " + args[0]);

        BufferedWriter out = new BufferedWriter(new FileWriter(args[0]));
        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
//        List<SearchData> searchDataList = searchDataService.getAllSearchDataForUpdate(statusParamMap);
        Iterator iterator = searchDataList.iterator();
        while (true) {
            if (!(iterator.hasNext())) break;
            searchData = (SearchData) iterator.next();
            StringTokenizer st = new StringTokenizer(searchData.getProductName());
            while(st.hasMoreTokens()) {
                out.write(st.nextToken());
                out.newLine();
            }
            indexCount++;
            if (indexCount % 1000 == 0) {
                logger.info(" Processing : " + indexCount);
            }
        }
        out.close();
        logger.info("======================================================");
        logger.info(String.format(" Total data count %d completed!!", indexCount));
    }
}
