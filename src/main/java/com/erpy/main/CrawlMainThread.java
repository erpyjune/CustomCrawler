package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.SB;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 15. 4. 13..
 */
public class CrawlMainThread extends Thread {
    private static Logger logger = Logger.getLogger(CrawlMainThread.class.getName());

    private String pageType;
    private int extractDataCount=0;
    private String crawlEncode;
    private String saveEncode;
    private String contentExtractCountPattern;
    private String extractType="html";

    private String crawlUrl;
    private String urlKeyword;
    private String cpName;
    private Map<String, CrawlData> allCrawlDatas;

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public int getExtractDataCount() {
        return extractDataCount;
    }

    public void setExtractDataCount(int extractDataCount) {
        this.extractDataCount = extractDataCount;
    }

    public String getCrawlEncode() {
        return crawlEncode;
    }

    public void setCrawlEncode(String crawlEncode) {
        this.crawlEncode = crawlEncode;
    }

    public String getSaveEncode() {
        return saveEncode;
    }

    public void setSaveEncode(String saveEncode) {
        this.saveEncode = saveEncode;
    }

    public String getContentExtractCountPattern() {
        return contentExtractCountPattern;
    }

    public void setContentExtractCountPattern(String contentExtractCountPattern) {
        this.contentExtractCountPattern = contentExtractCountPattern;
    }

    public String getCrawlUrl() {
        return crawlUrl;
    }

    public void setCrawlUrl(String crawlUrl) {
        this.crawlUrl = crawlUrl;
    }

    public String getUrlKeyword() {
        return urlKeyword;
    }

    public void setUrlKeyword(String urlKeyword) {
        this.urlKeyword = urlKeyword;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public Map<String, CrawlData> getAllCrawlDatas() {
        return allCrawlDatas;
    }

    public void setAllCrawlDatas(Map<String, CrawlData> allCrawlDatas) {
        this.allCrawlDatas = allCrawlDatas;
    }

    public String getExtractType() {
        return extractType;
    }

    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }

    public void run() {
        logger.info(String.format(" Thread run - %s", crawlUrl));
        CrawlIO crawlIO = new CrawlIO();
        crawlIO.setExtractType(extractType);
        crawlIO.setCrawlIO(pageType, extractDataCount, crawlEncode, saveEncode, contentExtractCountPattern);
        try {
            crawlIO.crawl(crawlUrl, urlKeyword, cpName, allCrawlDatas);
        } catch (Exception e) {
            logger.error(String.format(" Running exception - %s", cpName));
            e.printStackTrace();
        }
    }
}
