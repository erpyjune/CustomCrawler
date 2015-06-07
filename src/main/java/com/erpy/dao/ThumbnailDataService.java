package com.erpy.dao;

import com.erpy.DaoFactory.SearchDataMybatisFactory;
import com.erpy.DaoFactory.ThumbnailDataMybatisFactory;
import org.apache.ibatis.session.SqlSession;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public class ThumbnailDataService {
    public void insertThumbnailData(ThumbnailData thumbnailData) {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            thumbnailDataMapper.insertThumbnailData(thumbnailData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }


    public void getThumbnailDataById(int dataId) {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            thumbnailDataMapper.getThumbnailDataById(dataId);
        }finally{
            sqlSession.close();
        }
    }


    public ThumbnailData getFindThumbnailData(ThumbnailData thumbnailData) {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            return thumbnailDataMapper.getFindThumbnailData(thumbnailData);
        }finally{
            sqlSession.close();
        }
    }


    public void getAllThumbnailDatas() {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            thumbnailDataMapper.getAllThumbnailDatas();
        }finally{
            sqlSession.close();
        }
    }


    public void updateThumbnailData(ThumbnailData thumbnailData) {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            thumbnailDataMapper.updateThumbnailData(thumbnailData);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }


    public void deleteThumbnailData(int dataId) {
        SqlSession sqlSession = ThumbnailDataMybatisFactory.getSqlSessionFactory().openSession();
        try{
            ThumbnailDataMapper thumbnailDataMapper = sqlSession.getMapper(ThumbnailDataMapper.class);
            thumbnailDataMapper.deleteThumbnailData(dataId);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }


}
