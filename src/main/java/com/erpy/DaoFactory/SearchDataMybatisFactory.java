package com.erpy.DaoFactory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.Object;

import java.net.URL;
import java.net.URLClassLoader;



/**
 * Created by baeonejune on 14. 12. 28..
 */
public class SearchDataMybatisFactory {
	private static SqlSessionFactory factory;

	private SearchDataMybatisFactory() {
	}

	static {
		//InputStream is=null;
		Reader reader = null;
		
		try {
			//org reader = Resources.getResourceAsReader("searchdata-mybatis-config.xml");
			reader = Resources.getResourceAsReader("resources/searchdata-mybatis-config.xml");
			//is =  SearchDataMybatisFactory.class.getClassLoader().getResourceAsStream("/searchdata-mybatis-config.xml");
			//is =  Class.class.getClassLoader().getResourceAsStream("/resources/searchdata-mybatis-config.xml");
			//new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/config.txt")));

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		factory = new SqlSessionFactoryBuilder().build(reader);
		//factory = new SqlSessionFactoryBuilder().build(is);
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return factory;
	}
}
