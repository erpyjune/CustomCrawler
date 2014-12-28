package com.erpy.dao;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlData {
    Integer     dataId;
    String      seedUrl;
    String      crawlDate;
    String      savePath;
    String      cpName;
    String      crawlKeyword;

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
}
