package com.erpy.test;

import com.erpy.crawler.CrawlSite;

import java.io.IOException;

/**
 * Created by baeonejune on 14. 12. 30..
 */
public class IndexTest {

    static String url =
            "http://localhost:8080/HttpRequestSample/RequestSend.jsp";

    public static void main(String args[]) throws IOException {
        CrawlSite crawlSite = new CrawlSite();
        crawlSite.setCrawlUrl("http://localhost:9200/twitter/tweet/22");
        crawlSite.setCrawlData("{\"title\" : \"good morning\", \"name\" : \"erpy\", \"date\" : \"20141015\", \"id\" : 123}");
        crawlSite.HttpXPUT();
    }
}
