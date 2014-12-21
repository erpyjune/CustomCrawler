package com.erpy.dao;

import com.erpy.DaoFactory.SeedMybatisFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class SeedService {
    public void insertSeed(Seed seed) {
        SqlSession sqlSession = SeedMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SeedMapper seedMapper = sqlSession.getMapper(SeedMapper.class);
            seedMapper.insertSeed(seed);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }

    public Seed getSeedById(Integer seedId) {
        SqlSession sqlSession = SeedMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SeedMapper seedMapper = sqlSession.getMapper(SeedMapper.class);
            return seedMapper.getSeedById(seedId);
        }finally{
            sqlSession.close();
        }
    }

    public List<Seed> getAllSeeds() {
        SqlSession sqlSession = SeedMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SeedMapper seedMapper = sqlSession.getMapper(SeedMapper.class);
            return seedMapper.getAllSeeds();
        }finally{
            sqlSession.close();
        }
    }

    public void updateSeed(Seed seed) {
        SqlSession sqlSession = SeedMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SeedMapper seedMapper = sqlSession.getMapper(SeedMapper.class);
            seedMapper.updateSeed(seed);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }

    }

    public void deleteSeed(Integer seedId) {
        SqlSession sqlSession = SeedMybatisFactory.getSqlSessionFactory().openSession();
        try{
            SeedMapper seedMapper = sqlSession.getMapper(SeedMapper.class);
            seedMapper.deleteSeed(seedId);
            sqlSession.commit();
        }finally{
            sqlSession.close();
        }
    }
}
