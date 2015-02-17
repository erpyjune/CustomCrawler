package com.erpy.dao;

import java.util.List;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public interface CrawlDataMapper {
    public CrawlData getCrawlDataById(int dataId);
    public void insertCrawlData(CrawlData crawlData);
    public void updateCrawlData(CrawlData crawlData);
    public void deleteCrawlData(Integer seedId);
    public void deleteCrawlDataAll();
    public List<CrawlData> getAllCrawlDatas();
    public List<CrawlData> getCrawlDataByCpName(String cpName);
}
