package com.erpy.dao;

import com.erpy.DaoFactory.SearchDataMybatisFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 14. 12. 28..
 */
public class SearchDataService {
    public void insertSearchData(SearchData searchData) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper searchDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            searchDataMapper.insertSearchData(searchData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }

    public SearchData getSearchDataById(int dataId) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper crawlDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            return crawlDataMapper.getSearchDataById(dataId);
        }finally{
            sqlSession.close();
        }
    }

    public List<SearchData> getSearchDataByCpName(String cpName) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper crawlDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            return crawlDataMapper.getSearchDataByCpName(cpName);
        }finally{
            sqlSession.close();
        }
    }

    public List<SearchData> getAllSearchDatas() {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper crawlDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            return crawlDataMapper.getAllSearchDatas();
        }finally{
            sqlSession.close();
        }
    }

    public List<SearchData> getAllSearchDataForUpdate(Map<String, String> map) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper searchDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            return searchDataMapper.getAllSearchDataForUpdate(map);
        }finally{
            sqlSession.close();
        }
    }

    public void updateSearchData(SearchData searchData) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper searchDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            searchDataMapper.updateSearchData(searchData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }

    }

    public void updateSearchDataStatus(SearchData searchData) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper searchDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            searchDataMapper.updateSearchDataStatus(searchData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }

    }

    public void deleteSearchData(Integer seedId) {
        SqlSession sqlSession = SearchDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SearchDataMapper crawlDataMapper = sqlSession.getMapper(SearchDataMapper.class);
            crawlDataMapper.deleteSearchData(seedId);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }
}
