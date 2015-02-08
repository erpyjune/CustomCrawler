package com.erpy.test;

import com.erpy.crawler.CrawlSite;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.OkMallProc;
import com.erpy.utils.GlobalInfo;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by baeonejune on 15. 1. 25..
 */
public class SearchJsonTest {
    public static void main(String args[]) throws IOException {
        int returnCode;
        StringBuilder sb = new StringBuilder();
        StringBuilder indexUrl = new StringBuilder("http://localhost:9200/shop/okmall/");
        CrawlSite crawlSite = new CrawlSite();
        SearchData searchData = new SearchData();

        sb.append("{");

        sb.append("\"query\" : {");
        sb.append("\"term\" : {\"product_name\" : \"nike\"}");
        sb.append("}");
        sb.append("}");

        indexUrl.append(searchData.getDataId());
        crawlSite.setCrawlUrl(indexUrl.toString());
        crawlSite.setCrawlData(sb.toString());
        System.out.println("indexing url : " + indexUrl.toString());
        System.out.println("product name : " + searchData.getProductName());

        returnCode = crawlSite.HttpXPUT();
        System.out.println("return code : " + returnCode);
        System.out.println("---------------------------------------------");
    }
}
