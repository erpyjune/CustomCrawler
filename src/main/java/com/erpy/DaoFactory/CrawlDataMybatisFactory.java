package com.erpy.DaoFactory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlDataMybatisFactory {
    private static SqlSessionFactory factory;

    private CrawlDataMybatisFactory() {
    }

    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("seed-mybatis-config.xml");
            System.out.println("SeedMybatisFactory : " + reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }
}
