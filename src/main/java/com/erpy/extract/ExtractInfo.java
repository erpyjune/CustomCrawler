package com.erpy.extract;

import org.apache.ibatis.io.Resources;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by baeonejune on 15. 2. 8..
 */
public class ExtractInfo {
    
    private static ExtractProperties okmall = new ExtractProperties();
    
    public ExtractInfo() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("extract.properties");
        Properties props = new Properties();
        props.load(is);

        // okmall
        okmall.setListGroup(props.getProperty("okmall.select.ListGroup"));
        okmall.setSalePerGroup(props.getProperty("okmall.select.SalePerGroup"));
        okmall.setSalePer(props.getProperty("okmall.select.SalePer"));

        okmall.setOrgPriceGroup(props.getProperty("okmall.select.OrgPriceGroup"));
        okmall.setOrgOrgPrice(props.getProperty("okmall.select.OrgPrice"));

        okmall.setSalePriceGroup(props.getProperty("okmall.select.SalePriceGroup"));
        okmall.setSalePrice(props.getProperty("okmall.select.SalePrice"));

        okmall.setProductNameGroup(props.getProperty("okmall.select.ProductNameGroup"));
        okmall.setProductName(props.getProperty("okmall.select.ProductName"));

        okmall.setBrandNameGroup(props.getProperty("okmall.select.BrandNameGroup"));
        okmall.setBrandName(props.getProperty("okmall.select.BrandName"));

        okmall.setLinkGroup(props.getProperty("okmall.select.LinkGroup"));
        okmall.setLink(props.getProperty("okmall.select.Link"));
        okmall.setLinkAttr(props.getProperty("okmall.attr.Link"));

        okmall.setThumbGroup(props.getProperty("okmall.select.ThumbGroup"));
        okmall.setThumb(props.getProperty("okmall.select.Thumb"));
        okmall.setThumbAttr(props.getProperty("okmall.attr.Thumb"));
    }
    
    public ExtractProperties getOkmallProf() throws Exception {return okmall;}
}
