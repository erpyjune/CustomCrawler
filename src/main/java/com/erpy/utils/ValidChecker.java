package com.erpy.utils;

import com.erpy.dao.SearchData;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 15. 4. 6..
 */
public class ValidChecker {
    private static Logger logger = Logger.getLogger(ValidChecker.class.getName());

    private int skipCount=0;

    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    public Map<String, SearchData> checkSearchDataValid(
            Map<String, SearchData> allMap, Map<String, SearchData> partMap) throws Exception {

        String productId;
        SearchData searchDataPart;
        SearchData searchDataAll;
        Map<String, SearchData> newSearchDataMap = new HashMap<String, SearchData>();
        GlobalUtils globalUtils = new GlobalUtils();

        for(Map.Entry<String, SearchData> entry : partMap.entrySet()) {
            productId = entry.getKey();
            searchDataPart = entry.getValue();

            if (globalUtils.isDataEmpty(searchDataPart)) {
                logger.error(String.format(" Null 데이터가 있어서 skip 합니다 (%s)",
                        searchDataPart.getProductId()));
                continue;
            }

            // 기존 추출된 데이터가 이미 존재하는 경우.
            if (allMap.containsKey(productId)) {
                // 기존 데이터에서 하나 꺼내서.
                searchDataAll = allMap.get(productId);
                if (globalUtils.isDataEmpty(searchDataAll)) {
                    logger.error(String.format(" 기존 데이중에 Null 데이터가 있어서 skip 합니다. prdid(%s)",
                            searchDataAll.getProductId()));
                    continue;
                }

                // 동일한 데이터가 있는지 비교한다.
                if (searchDataAll.getSalePrice().equals(searchDataPart.getSalePrice())
                        && searchDataAll.getProductName().equals(searchDataPart.getProductName())
                        && searchDataAll.getOrgPrice().equals(searchDataPart.getOrgPrice())) {

                    skipCount++;

                    // 동일한 데이터가 있으면 아무것도 안한다.
                    logger.info(String.format(" SAME DATA (%s)(%d)(%d)(%s)",
                            searchDataAll.getProductId(),
                            searchDataAll.getSalePrice(),
                            searchDataAll.getOrgPrice(),
                            searchDataAll.getProductName()));
                }
                // product id는 동일하지만 필드 값이 다른경우.
                else {
                    logger.info(String.format(" UPDATE (%s)(%f)(%d)(%d)(%s)(%s)",
                            searchDataPart.getProductId(),
                            searchDataPart.getSalePer(),
                            searchDataPart.getSalePrice(),
                            searchDataPart.getOrgPrice(),
                            searchDataPart.getProductName(),
                            searchDataPart.getContentUrl()));

                    searchDataPart.setType("update");
                    searchDataPart.setDataStatus("U");
                    // 변경된 데이터가 있기 때문에 해당 db에 업데이트를 하기 위해 id 셋팅.
                    searchDataPart.setDataId(searchDataAll.getDataId());
                    newSearchDataMap.put(productId, searchDataPart);
                }
            }
            // 동일한 product id가 없는 경우
            else {
                logger.info(String.format(" INSERT (%s)(%f)(%d)(%d)(%s)(%s)",
                        searchDataPart.getProductId(),
                        searchDataPart.getSalePer(),
                        searchDataPart.getSalePrice(),
                        searchDataPart.getOrgPrice(),
                        searchDataPart.getProductName(),
                        searchDataPart.getContentUrl()));

                searchDataPart.setType("insert");
                searchDataPart.setDataStatus("I");
                newSearchDataMap.put(productId, searchDataPart);
            }
        }

        return newSearchDataMap;
    }
}
