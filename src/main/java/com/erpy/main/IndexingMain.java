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

    private static final String IndexingUrl = "http://localhost:9200/shop/";

    public static void main (String args[]) throws IOException {
        SearchData searchData = new SearchData();
        SearchDataService searchDataService = new SearchDataService();
        OkMallProc okMallProc = new OkMallProc();

        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator iterator = searchDataList.iterator();
        while (true) {
            if (!(iterator.hasNext())) break;
            searchData = (SearchData)iterator.next();
//            System.out.println(String.format("(%d)%s",searchData.getDataId(), searchData.getCpName()));
            if (searchData.getCpName()==null) continue;;
            if (searchData.getCpName().equals(GlobalInfo.CP_OKMALL)) {
                okMallProc.indexingOkMall(searchData);
            } else {
                System.out.println("cp name is not equals !!");
            }
        }
    }
}
