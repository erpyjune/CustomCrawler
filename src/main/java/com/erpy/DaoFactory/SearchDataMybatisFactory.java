package com.erpy.DaoFactory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by baeonejune on 14. 12. 28..
 */
public class SearchDataMybatisFactory {
    private static SqlSessionFactory factory;

    private SearchDataMybatisFactory() {
    }

    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("searchdata-mybatis-config.xml");
            System.out.println("SearchDataMybatisFactory : " + reader);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }
}
