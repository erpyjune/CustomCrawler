package com.erpy.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 14. 12. 28..
 */
public interface SearchDataMapper {
    public SearchData getSearchDataById(int dataId);
    public void insertSearchData(SearchData searchData);
    public void updateSearchData(SearchData searchData);
    public void updateSearchDataStatus(SearchData searchData);
    public void deleteSearchData(Integer prodId);
    public List<SearchData> getAllSearchDatas();
    public List<SearchData> getAllSearchDataForUpdate(Map<String,String> map);
    public List<SearchData> getSearchDataByCpName(String cpName);
}
