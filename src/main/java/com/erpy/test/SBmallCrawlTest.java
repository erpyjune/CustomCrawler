package com.erpy.test;

import com.erpy.crawler.CrawlSite;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class SBmallCrawlTest {
    public static void main(String[] args) throws Exception {
        CrawlSite cw = new CrawlSite();

        cw.setCrawlUrl("http://sbclub.co.kr/search_brandproductlist.html");
        cw.setCrawlEncode("UTF-8");
        cw.setConnectionTimeout(1000);
        cw.setSocketTimeout(5000);

        cw.addPostRequestParam("mode","categorymain");
        cw.addPostRequestParam("categoryid","94201");
        cw.addPostRequestParam("startnum","1");
        cw.addPostRequestParam("endnum","40");

//        cw.HttpCrawlGetDataTimeout();
        cw.HttpPostGet();

        System.out.println(cw.getCrawlData());
    }
}
