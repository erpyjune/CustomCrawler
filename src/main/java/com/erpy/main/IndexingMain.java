package com.erpy.main;

import com.erpy.crawler.CrawlSite;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by baeonejune on 14. 12. 30..
 */
public class IndexingMain {

    private static SearchDataService searchDataService;
    private static final String IndexingUrl = "http://localhost:9200/shop/";

    public static void main (String args[]) throws IOException {
        StringBuilder sb = new StringBuilder();
        SearchData searchData = new SearchData();
        searchDataService = new SearchDataService();
        CrawlSite crawlSite = new CrawlSite();
        OkMallProc okMallProc = new OkMallProc();
        GlobalInfo globalInfo = new GlobalInfo();

        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator iterator = searchDataList.iterator();
        while (iterator.hasNext()) {
            searchData = (SearchData)iterator.next();
            //System.out.println(String.format("(%d)%s",searchData.getDataId(), searchData.getProductName()));
            if (searchData.getCpName().equals(globalInfo.CP_OKMALL)) {
                okMallProc.indexingOkMall(searchData);
            } else {
                System.out.println("cp name is not equals !!");
            }
        }
    }
}
