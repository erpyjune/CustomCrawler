package com.erpy.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 14. 12. 28..
 */
public interface SearchDataMapper {
    SearchData getSearchDataById(int dataId);
    void insertSearchData(SearchData searchData);
    void updateSearchData(SearchData searchData);
    void updateSearchDataStatus(SearchData searchData);
    void deleteSearchData(Integer prodId);
    List<SearchData> getAllSearchDatas();
    List<SearchData> getAllSearchDataForUpdate(Map<String,String> map);
    List<SearchData> getSearchDataByCpName(String cpName);
    List<SearchData> getSearchDataByCpNameBigThumbFieldNULL(String cpName);
}
