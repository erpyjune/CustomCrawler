package com.erpy.utils;

import com.erpy.crawler.CrawlSite;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created by baeonejune on 15. 3. 30..
 */
public class GlobalUtils {
    private static Logger logger = Logger.getLogger(GlobalUtils.class.getName());
    public boolean isDataEmpty(SearchData sd) {
        if (sd.getOrgPrice() == null) return true;
        if (sd.getSalePrice() == null) return true;
        if (sd.getProductName() == null) return true;
        if (sd.getProductId() == null) return true;
        if (sd.getCpName() == null) return true;
        if (sd.getContentUrl() == null) return true;
        if (sd.getThumbUrl() == null) return true;

        if (sd.getProductName().isEmpty()) return true;
        if (sd.getCpName().isEmpty()) return true;
        if (sd.getContentUrl().isEmpty()) return true;
        if (sd.getThumbUrl().isEmpty()) return true;

        return false;
    }

    public static boolean isAllDigitChar(String s) {
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllFloatChar(String s) {
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch) && !(ch == '.')) {
                return false;
            }
        }
        return true;
    }

    public String getFieldData(String src, String startTag, String endTag) {
        if (src==null || startTag==null || endTag==null) return "";
        int spos = src.indexOf(startTag);
        if (spos<0) return "";
        int epos = src.indexOf(endTag, spos);
        return src.substring(spos+startTag.length(), epos);
    }

    public String getFieldData(String src, String startTag) {
        if (src==null || startTag==null) return "";
        int spos = src.indexOf(startTag);
        if (spos<0) return "";
        return src.substring(spos+startTag.length());
    }

    public String makeSaveFilePath(String prefixPath, String cpName, Integer randomNumber) {
        return prefixPath + "/" + cpName + "/" + Integer.toString(randomNumber) + ".html";
    }

    public boolean saveDirCheck(String savePrefixPath, String cpName) {
        try {
            File dir = new File(savePrefixPath + "/" + cpName);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
        catch (Exception e) {
            logger.error(e.getStackTrace());
            return false;
        }
        return true;
    }

    public boolean isSaveFilePathCollision(String filePath) {
        File f = new File(filePath);
        if(f.exists()) {
            logger.error(String.format(" 저장할 파일 이름이 충돌 납니다 - %s ", filePath));
            return false;
        }
        return true;
    }

    public String priceDataCleaner(String s) {
        if (s==null) return "";
        return s.replace("원", "").replace("won","").replace(",", "").
                replace("<b>","").replace("</b>","").replace("판매가","").replace(" ","").
                replace(":","").replace("이벤트가","").trim();
    }

    public String htmlCleaner(String s) {
        if (s==null) return "";
        return s.replace("&lt;", "<").replace("&gt;",">").replace("[", "").replace("]", "").replace(",", " ");
    }

    public int checkDataCount(String path, String pattern, String readEncoding) throws IOException {
        FileIO fileIO = new FileIO();
        fileIO.setPath(path);
        fileIO.setEncoding(readEncoding);

        String data = fileIO.getFileContent();
        Document doc = Jsoup.parse(data);
        Elements elements = doc.select(pattern);
        return elements.size();
    }

    public String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }

    public int indexingES(SearchData searchData) throws Exception {
        int returnCode;
        StringBuffer sb = new StringBuffer();
        StringBuffer indexUrl = new StringBuffer("http://localhost:9200/shop/okmall/");
        CrawlSite crawlSite = new CrawlSite();

        sb.append("{");

        sb.append("\"dataid\" : ");
        sb.append("\"").append(searchData.getDataId()).append("\",");

        sb.append("\"product_name\" : ");
        sb.append("\"").append(searchData.getProductName()).append("\",");

        sb.append("\"brand_name\" : ");
        sb.append("\"").append(searchData.getBrandName()).append("\",");

        sb.append("\"url\" : ");
        sb.append("\"http://www.okmall.com").append(searchData.getContentUrl()).append("\",");

        sb.append("\"thumb\" : ");
        sb.append("\"").append(searchData.getThumbUrl()).append("\",");

        sb.append("\"org_price\" : ");
        sb.append(" ").append(searchData.getOrgPrice()).append(",");

        sb.append("\"sale_price\" : ");
        sb.append(" ").append(searchData.getSalePrice()).append(",");

        sb.append("\"sale_per\" : ");
        sb.append(" ").append(searchData.getSalePer()).append(",");

        sb.append("\"cp\" : ");
        sb.append("\"").append(searchData.getCpName()).append("\",");

        sb.append("\"keyword\" : ");
        sb.append("\"").append(searchData.getCrawlKeyword()).append("\"");

        sb.append("}");

        // set docid
        indexUrl.append(searchData.getProductId());
        // set crawl url
        crawlSite.setCrawlUrl(indexUrl.toString());
        // set crawl url data
        crawlSite.setCrawlData(sb.toString());

        // indexing request
        returnCode = crawlSite.HttpXPUT();

        if (returnCode == 200 || returnCode == 201) {
            logger.info(String.format(" Indexing [ %d ] %s - %s",
                    returnCode,
                    searchData.getProductId(),
                    searchData.getProductName()));
        }
        else {
            logger.error(String.format(" Indexing [ %d ] %s - %s",
                    returnCode,
                    searchData.getProductId(),
                    searchData.getProductName()));
        }

        return returnCode;
    }
}
