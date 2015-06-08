package com.erpy.DaoFactory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by baeonejune on 15. 6. 7..
 */
public class ThumbnailDataMybatisFactory {
    private static SqlSessionFactory factory;

    private ThumbnailDataMybatisFactory() {
    }

    static {
        Reader reader;
        try {
            reader = Resources.getResourceAsReader("thumbnail-mybatis-config.xml");
            System.out.println("ThumbnailDataMybatisFactory : " + reader);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }
}
