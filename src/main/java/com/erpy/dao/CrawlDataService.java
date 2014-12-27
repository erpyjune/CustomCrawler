package com.erpy.dao;

import com.erpy.DaoFactory.CrawlDataMybatisFactory;
import com.erpy.DaoFactory.SeedMybatisFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlDataService {
    public void insertCrawlData(CrawlData crawlData) {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            crawlDataMapper.insertCrawlData(crawlData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }

    public CrawlData getCrawlDataById(int dataId) {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            return crawlDataMapper.getCrawlDataById(dataId);
        }finally{
            sqlSession.close();
        }
    }

    public List<CrawlData> getCrawlDataByCpName(String cpName) {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            return crawlDataMapper.getCrawlDataByCpName(cpName);
        }finally{
            sqlSession.close();
        }
    }

    public List<CrawlData> getAllCrawlDatas() {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            return crawlDataMapper.getAllCrawlDatas();
        }finally{
            sqlSession.close();
        }
    }

    public void updateCrawlData(CrawlData crawlData) {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            crawlDataMapper.updateCrawlData(crawlData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }

    }

    public void deleteCrawlData(Integer seedId) {
        SqlSession sqlSession = CrawlDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            CrawlDataMapper crawlDataMapper = sqlSession.getMapper(CrawlDataMapper.class);
            crawlDataMapper.deleteCrawlData(seedId);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }
}
