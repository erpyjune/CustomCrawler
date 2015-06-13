package com.erpy.utils;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.dao.ThumbnailData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
        int epos = src.indexOf(endTag, spos+1);
        if (epos<0) {
            epos = src.length(); // 맨끌 길이를 준다.
        }
        return src.substring(spos+startTag.length(), epos);
    }

    public String getFieldData(String src, String startTag) {
        if (src==null || startTag==null) return "";
        int spos = src.indexOf(startTag);
        if (spos<0) return "";
        if ((spos + startTag.length()) > src.length()) return "";
        return src.substring(spos + startTag.length());
    }

    public String priceDataCleaner(String s) {
        if (s==null) return "";
        return s.replace("원", "").replace("won", "").replace(",", "").
                replace("<b>", "").replace("</b>", "").replace("판매가", "").replace(" ","").
                replace(":", "").replace("이벤트가", "").replace("개 구매중","").replace("개구매중","").
                replace("%", "").replace("~","").replace("개","").replace("구매","").replace("HIT","").
                replace("할인가","").trim();
    }

    ////////////////////////////////////////////////////////////////////////////
    public String htmlCleaner(String s) {
        if (s==null) return "";
        return s.replace("&lt;", "<").replace("&gt;",">").replace("[", "").replace("]", "").replace(",", " ");
    }

    ////////////////////////////////////////////////////////////////////////////
    public int checkDataCountContent(String data, String pattern) throws IOException {
        if (pattern==null || pattern.length()==0) {
            logger.error(" pattern is NULL !!");
            return 0;
        }
        Document doc = Jsoup.parse(data);
        Elements elements = doc.select(pattern);
        return elements.size();
    }

    ////////////////////////////////////////////////////////////////////////////
    public int checkDataCountContentJson(String data, String pattern) throws IOException {
        if (pattern==null || pattern.length()==0) {
            logger.error(" pattern is NULL !!");
            return 0;
        }

        int count=0;
        JsonNode node;
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonData = data.trim().replace("\n","").getBytes();
        JsonNode rootNode = objectMapper.readTree(jsonData).path(pattern);
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            node = iter.next();
            count++;
        }
        return count;
    }

    ////////////////////////////////////////////////////////////////////////////
    public String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            logger.error(String.format(" Hash value generated - (%s)", md5));
        }
        return "";
    }

    ////////////////////////////////////////////////////////////////////////////
    public void saveDiskImgage(String localPath, String cpName, String url, String fileName) throws Exception {
        String file_ext = fileName.substring (
                fileName.lastIndexOf('.') + 1,
                fileName.length() );

        BufferedImage image;

        image = ImageIO.read(new URL(url));
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_BGR);

        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.drawImage(image, 0, 0, null);

        ImageIO.write(bufferedImage, file_ext, new File(localPath + "/" + cpName + "/" + fileName));
        logger.info(fileName + " Downloaded : " + url);
    }

    ////////////////////////////////////////////////////////////////////////////
    public String splieImageFileName(String url) {
        String[] array = url.split("/");
        return array[array.length-1];
    }


    ////////////////////////////////////////////////////////////////////////////
    public int indexingES(SearchData searchData, ThumbnailData thumb) throws Exception {
        int returnCode;
        StringBuilder sb = new StringBuilder();
//        StringBuilder indexUrl = new StringBuilder("http://localhost:9200/shop/okmall/");
        StringBuilder indexUrl = new StringBuilder("http://summarynode.cafe24.com:9200/shop/okmall/");
        CrawlSite crawlSite = new CrawlSite();


        sb.append("{");

        sb.append("\"dataid\" : ");
        sb.append("\"").append(searchData.getDataId()).append("\",");

        sb.append("\"product_name\" : ");
        sb.append("\"").append(JSONObject.escape(searchData.getProductName())).append("\",");

        sb.append("\"brand_name\" : ");
        sb.append("\"").append(JSONObject.escape(searchData.getBrandName())).append("\",");

        sb.append("\"url\" : ");
        sb.append("\"").append(searchData.getContentUrl()).append("\",");

        sb.append("\"thumb\" : ");
        sb.append("\"").append(searchData.getThumbUrl()).append("\",");

        sb.append("\"bthumb\" : ");
        sb.append("\"").append(getImageUrl(thumb.getBigThumbUrl())).append("\",");

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
            logger.info(String.format(" Indexing [ %d ] %s|%s|%s",
                    returnCode,
                    searchData.getCpName(),
                    searchData.getProductId(),
                    searchData.getProductName()));
        }
        else {
            logger.error(String.format(" Indexing [ %d ] %s|%s|%s",
                    returnCode,
                    searchData.getCpName(),
                    searchData.getProductId(),
                    searchData.getProductName()));
        }
        return returnCode;
    }


    ////////////////////////////////////////////////////////////////////////////
    public String getImageUrl(String thumbUrl) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (thumbUrl.contains("airmt.net")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/airmt/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("tongoutdoor.com")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/tongoutdoor/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("sbclub.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/sbclub/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("gogo337.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/gogo337/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("121.254.171.83")) { // okmall
            sb = sb.append("http://summarynode.cafe24.com/gimages/okmall/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("chocammall.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/first/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("dicamping.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/dicamping/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("campingmall.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/dicamping/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("campingmall.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/dicamping/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("totooutdoor.com")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/totooutdoor/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("weekenders.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/weekenders/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("dkmountain.com")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/starus/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("leisureman.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/leisureman/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("campingon.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/campingon/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("niio.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/niio/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("ccamping.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/ccamping/").append(splieImageFileName(thumbUrl));
        }
        else if (thumbUrl.contains("camptown.co.kr")) {
            sb = sb.append("http://summarynode.cafe24.com/gimages/camptown/").append(splieImageFileName(thumbUrl));
        }

        return sb.toString();
    }


    ////////////////////////////////////////////////////////////////////////////
    public void makeThumbnail(String filePath, String outputFile) throws Exception {
        try {
            BufferedImage sourceImage = ImageIO.read(new File(filePath));
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();

            if(width>height){
                float extraSize=    height-100;
                float percentHight = (extraSize/height)*100;
                float percentWidth = width - ((width/100)*percentHight);
                BufferedImage img = new BufferedImage((int)percentWidth, 100, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance((int)percentWidth, 100, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage((int)((percentWidth-100)/2), 0, 100, 100);


                ImageIO.write(img2, "jpg", new File(outputFile));
            }else{
                float extraSize=    width-100;
                float percentWidth = (extraSize/width)*100;
                float  percentHight = height - ((height/100)*percentWidth);
                BufferedImage img = new BufferedImage(100, (int)percentHight, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance(100,(int)percentHight, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage(0, (int)((percentHight-100)/2), 100, 100);


                ImageIO.write(img2, "jpg", new File(outputFile));
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    public void makeThumbnailTest(String filePath, String outputFile) throws Exception {
        try {
            BufferedImage sourceImage = ImageIO.read(new File(filePath));
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();

            if(width>height){
                float extraSize=    height-100;
                float percentHight = (extraSize/height)*100;
                float percentWidth = width - ((width/100)*percentHight);
                BufferedImage img = new BufferedImage((int)percentWidth, 100, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance((int)percentWidth, 100, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage((int)((percentWidth-100)/2), 0, 100, 100);
                ImageIO.write(img2, "jpg", new File(outputFile));
            }else{
                float extraSize=    width-100;
                float percentWidth = (extraSize/width)*100;
                float  percentHight = height - ((height/100)*percentWidth);
                BufferedImage img = new BufferedImage(100, (int)percentHight, BufferedImage.TYPE_INT_RGB);
                Image scaledImage = sourceImage.getScaledInstance(100,(int)percentHight, Image.SCALE_SMOOTH);
                img.createGraphics().drawImage(scaledImage, 0, 0, null);
                BufferedImage img2 = new BufferedImage(100, 100 ,BufferedImage.TYPE_INT_RGB);
                img2 = img.getSubimage(0, (int)((percentHight-100)/2), 100, 100);
                ImageIO.write(img2, "jpg", new File(outputFile));
            }
        } catch (Exception e) {
            logger.error(e.getStackTrace().toString());
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    public boolean makeThumbnail2(String loadFile, String saveFile, int maxDim)
            throws IOException {
        File save = new File(saveFile.replaceAll("/", "\\" + File.separator));
        FileInputStream fis = new FileInputStream(loadFile.replaceAll("/", "\\"
                + File.separator));
        BufferedImage im = ImageIO.read(fis);
        Image inImage = new ImageIcon(loadFile).getImage();
        double scale = (double) maxDim / (double) inImage.getHeight(null);
        if (inImage.getWidth(null) > inImage.getHeight(null)) {
            scale = (double) maxDim / (double) inImage.getWidth(null);
        }
        int scaledW = (int) (scale * inImage.getWidth(null));
        int scaledH = (int) (scale * inImage.getHeight(null));
        BufferedImage thumb = new BufferedImage(scaledW, scaledH,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = thumb.createGraphics();
        g2.drawImage(im, 0, 0, scaledW, scaledH, null);
        return ImageIO.write(thumb, "jpg", save);
    }


    ///////////////////////////////////////////////////////////////////
    // cp에 해당되는 모든 search table 데이터를 내린다.
    public Map<String, SearchData> getAllSearchDatasByCP(String cpName) throws Exception {
        Map<String, SearchData> allSearchDataMap = new HashMap<String, SearchData>();
        SearchDataService searchDataService = new SearchDataService();
        SearchData searchData;
        int existCount=0;

        java.util.List<SearchData> searchDatas = searchDataService.getSearchDataByCpName(cpName);
        Iterator searchDataIterator = searchDatas.iterator();
        while (searchDataIterator.hasNext()) {
            searchData = (SearchData) searchDataIterator.next();
            if (allSearchDataMap.containsKey(searchData.getProductId())) {
                existCount++;
            }
//            logger.info(String.format(" All Crawling DB Key(%s)", crawlData.getHashMD5() + crawlData.getCpName()));
            allSearchDataMap.put(searchData.getProductId(), searchData);
        }
        logger.info(String.format(" [%s]에서 가져온 데이터 - Total(%d), Exist(%d)", cpName, allSearchDataMap.size(), existCount));
        return allSearchDataMap;
    }


    ///////////////////////////////////////////////////////////////////
    // cp에 해당되는 모든 search table 데이터를 내린다.
    public Map<String, SearchData> getAllSearchDatasByCPBigThumbFieldNULL(String cpName) throws Exception {
        Map<String, SearchData> allSearchDataMap = new HashMap<String, SearchData>();
        SearchDataService searchDataService = new SearchDataService();
        SearchData searchData;
        int existCount=0;

        java.util.List<SearchData> searchDatas = searchDataService.getSearchDataByCpNameBigthumbFieldNULL(cpName);
        Iterator searchDataIterator = searchDatas.iterator();
        while (searchDataIterator.hasNext()) {
            searchData = (SearchData) searchDataIterator.next();
            if (allSearchDataMap.containsKey(searchData.getProductId())) {
                existCount++;
            }
//            logger.info(String.format(" All Crawling DB Key(%s)", crawlData.getHashMD5() + crawlData.getCpName()));
            allSearchDataMap.put(searchData.getProductId(), searchData);
        }
        logger.info(String.format(" [%s]에서 가져온 데이터 - Total(%d), Exist(%d)", cpName, allSearchDataMap.size(), existCount));
        return allSearchDataMap;
    }
}
