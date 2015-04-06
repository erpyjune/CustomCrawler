package com.erpy.utils;

import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by baeonejune on 15. 4. 6..
 */
public class DB {
    private static Logger logger = Logger.getLogger(DB.class.getName());
    private int unknownCount=0;
    private int insertCount=0;
    private int updateCount=0;

    public int getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(int unknownCount) {
        this.unknownCount = unknownCount;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public void updateToDB(Map<String, SearchData> sdAll) throws IOException {
        SearchDataService searchDataService = new SearchDataService();
        SearchData sd;

        for(Map.Entry<String, SearchData> entry : sdAll.entrySet()) {
            //strUrlLink = entry.getKey();
            sd = entry.getValue();

            // insert or update 타입이 없는 경우
            if (sd.getType().isEmpty()) {
                logger.warn(String.format(" UPDATED(empty) : %s|%s|%s",
                        sd.getCpName(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                unknownCount++;
            }
            else if (sd.getType().equals("insert")) {
                logger.info(String.format(" INSERTED : %s|%d|%d|%s|%s",
                        sd.getCpName(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                insertCount++;
            }
            else if (sd.getType().equals("update")) {
                logger.info(String.format(" UPDATED : %s|%d|%d|%s|%s",
                        sd.getCpName(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.updateSearchData(sd);
                updateCount++;
            }
            else {
                logger.error(String.format(" Data business is not (inset or update) %d", sd.getDataId()));
            }
        }
    }
}
