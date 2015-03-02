package com.erpy.test;

import com.erpy.crawler.CrawlSite;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class CrawlTest {
    public static void main(String[] args) throws Exception {
        CrawlSite cw = new CrawlSite();

        cw.setCrawlUrl("http://www.sbclub.co.kr/search_brandproductlist.html");
//        cw.setCrawlUrl("http://www.sbclub.co.kr/category01.html?categoryid=9420102");
        cw.setCrawlEncode("UTF-8");
        cw.setConnectionTimeout(1000);
        cw.setSocketTimeout(5000);

//        cw.HttpCrawlGetDataTimeout();
        cw.HttpPostGet();

        System.out.println(cw.getCrawlData());
    }
}
