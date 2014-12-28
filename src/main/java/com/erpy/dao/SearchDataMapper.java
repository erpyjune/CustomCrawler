package com.erpy.dao;

import java.util.List;

/**
 * Created by baeonejune on 14. 12. 28..
 */
public interface SearchDataMapper {
    public SearchData getSearchDataById(int dataId);
    public void insertSearchData(SearchData crawlData);
    public void updateSearchData(SearchData crawlData);
    public void deleteSearchData(Integer prodId);
    public List<SearchData> getAllSearchDatas();
    public List<SearchData> getSearchDataByCpName(String cpName);
}
