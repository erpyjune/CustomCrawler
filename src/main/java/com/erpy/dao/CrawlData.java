package com.erpy.dao;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlData {
    Integer dataId;
    String seedUrl;
    String crawlDate;
    String savePath;
    String cpName;
    String crawlKeyword;
    String cateName1;
    String cateName2;
    String hashMD5;

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public String getCrawlDate() {
        return crawlDate;
    }

    public void setCrawlDate(String crawlDate) {
        this.crawlDate = crawlDate;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getCrawlKeyword() {
        return crawlKeyword;
    }

    public void setCrawlKeyword(String crawlKeyword) {
        this.crawlKeyword = crawlKeyword;
    }

    public String getCateName1() {
        return cateName1;
    }

    public void setCateName1(String cateName1) {
        this.cateName1 = cateName1;
    }

    public String getCateName2() {
        return cateName2;
    }

    public void setCateName2(String cateName2) {
        this.cateName2 = cateName2;
    }

    public String getHashMD5() {
        return hashMD5;
    }

    public void setHashMD5(String hashMD5) {
        this.hashMD5 = hashMD5;
    }
}
