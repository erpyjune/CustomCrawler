package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import com.erpy.utils.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by baeonejune on 15. 4. 5..
 */
public class CampingPlus {
    private static final String prefixContentUrl = "http://www.camping-plus.co.kr/mall/m_mall_detail.php?ps_goid=";
    private static final String prefixHostThumbUrl = "http://www.camping-plus.co.kr";
    private static Logger logger = Logger.getLogger(CampingPlus.class.getName());
    private GlobalUtils globalUtils = new GlobalUtils();
    private ValidChecker validChecker = new ValidChecker();
    private DB db = new DB();
    // for extract.
    private int totalExtractCount=0;
    private int skipCount=0;
    private int insertCount=0;
    private int updateCount=0;
    private int unknownCount=0;
    // for crawling.
    private int crawlCount=0;
    private int crawlErrorCount=0;
    private int collisionFileCount=0;
    private String filePath;
    private String keyword;
    private String txtEncode="utf-8";
    private String seedUrl;

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getTxtEncode() {
        return txtEncode;
    }

    public void setTxtEncode(String txtEncode) {
        this.txtEncode = txtEncode;
    }

    public int getTotalExtractCount() {
        return totalExtractCount;
    }

    public void setTotalExtractCount(int totalExtractCount) {
        this.totalExtractCount = totalExtractCount;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(int skipCount) {
        this.skipCount = skipCount;
    }

    public int getInsertCount() {
        return insertCount;
    }

    public void setInsertCount(int insertCount) {
        this.insertCount = insertCount;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getUnknownCount() {
        return unknownCount;
    }

    public void setUnknownCount(int unknownCount) {
        this.unknownCount = unknownCount;
    }

    public int getCrawlCount() {
        return crawlCount;
    }

    public void setCrawlCount(int crawlCount) {
        this.crawlCount = crawlCount;
    }

    public int getCrawlErrorCount() {
        return crawlErrorCount;
    }

    public void setCrawlErrorCount(int crawlErrorCount) {
        this.crawlErrorCount = crawlErrorCount;
    }

    public int getCollisionFileCount() {
        return collisionFileCount;
    }

    public void setCollisionFileCount(int collisionFileCount) {
        this.collisionFileCount = collisionFileCount;
    }

    public Map<String, SearchData> extract(CrawlData crawlData) throws Exception {
        FileIO fileIO = new FileIO();
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl;


        if (filePath==null) {
            logger.fatal(" FilePath is null !!");
            throw new Exception("Extract file path is null!!");
        }

        fileIO.setEncoding(txtEncode);
        fileIO.setPath(filePath);

        String htmlContent;
        try {
            htmlContent = fileIO.getFileContent();
        } catch (Exception e) {
            logger.error(String.format(" File exist not - (%s)", filePath));
            return searchDataMap;
        }

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("table[width=\"130\"]");
        for (Element element : elements) {

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            if (!element.outerHtml().contains("padding-top:8px;text-align:center"))
                continue;

            // Thumb link
            listE = document.select("div[style=\"border:1px solid #D2D2D2;margin:0 0 5px 0;padding:2px\"] img");
            for (Element et : listE) {
                strItem = et.attr("src");
                if (strItem.contains("./shop_image")) {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem.replace("./shop_image", "/mall/shop_image"));
                } else {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                }
                logger.debug(String.format(" >> Thumb (%s)", searchData.getThumbUrl()));
            }

            // link
            listE = document.select("div[style=\"border:1px solid #D2D2D2;margin:0 0 5px 0;padding:2px\"] a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "ps_goid=");
                    searchData.setContentUrl(prefixContentUrl + productId);
                    searchData.setProductId(productId);
                    logger.debug(String.format(" >> Link (%s)", searchData.getContentUrl()));
                }
            }

            // product name
            listE = document.select("div.goods_name_");
            for (Element et : listE) {
                searchData.setProductName(et.text().trim());
                logger.debug(String.format(" >> title (%s)", searchData.getProductName()));
            }

            // org price
            listE = document.select("div.market_price_ strike");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setOrgPrice(Integer.parseInt(strItem));
//                    logger.debug(String.format(" >> org price (%s)", searchData.getOrgPrice()));
                    break;
                } else {
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", crawlData.getSeedUrl()));
                }
            }

            // sale price
            listE = document.select("div.price_");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    searchData.setSalePer(0.0F);
//                    logger.info(String.format(" >> sale price(%s)", searchData.getSalePrice()));
                    break;
                } else {
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", crawlData.getSeedUrl()));
                }
            }

            if (searchData.getOrgPrice()>0 && searchData.getSalePrice()==0) {
                searchData.setSalePrice(searchData.getOrgPrice());
            }
            if (searchData.getSalePrice()>0 && searchData.getOrgPrice()==0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_CampingPlus);
            // set keyword.
            searchData.setCrawlKeyword(keyword);
            // set seed url
            searchData.setSeedUrl(seedUrl);
            // cate code
            searchData.setCateName1(crawlData.getCateName1());
            searchData.setCateName2(crawlData.getCateName2());
            searchData.setCateName3(crawlData.getCateName3());

            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!globalUtils.isDataEmpty(searchData)) {
                // key : product id
                searchDataMap.put(productId + searchData.getCpName(), searchData);
                totalExtractCount++;
            }
        }

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }

    public void mainExtractProcessing(CampingPlus cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap;
        Map<String, SearchData> newSearchDataMap;

        cp.setFilePath(crawlData.getSavePath());
        cp.setKeyword(crawlData.getCrawlKeyword());
        cp.setSeedUrl(crawlData.getSeedUrl());

        // 데이터 추출.
        searchDataMap = cp.extract(crawlData);
        if (searchDataMap.size() <= 0) {
            logger.error(String.format(" 이 파일은 추출된 데이터가 없습니다 (%s)",crawlData.getSavePath()));
            return ;
        }

        // DB에 들어있는 데이터와 쇼핑몰에서 가져온 데이터를 비교한다.
        // 비교결과 update, insert할 데이터를 모아서 리턴 한다.
        newSearchDataMap = validChecker.checkSearchDataValid(allSearchDatasMap, searchDataMap);
        if (newSearchDataMap.size() <= 0) {
            logger.info(String.format(" 변경되거나 새로 생성된 상품 데이터가 없습니다 - %s", crawlData.getSavePath()));
        }
        else {
            // db에 추출한 데이터를 넣는다.
            db.updateToDB(newSearchDataMap);

            // insert 되거나 update된 데이터들을 다시 allSearchDataMap에 입력하여
            // 새로 parsing되서 체크하는 데이터 비교에 반영될 수 있도록 한다.
            String productId;
            SearchData tmpSD;
            for(Map.Entry<String, SearchData> entry : newSearchDataMap.entrySet()) {
                productId = entry.getKey().trim();
                tmpSD = entry.getValue();

                logger.debug(String.format("Key(%s), PrdName(%s)", productId, tmpSD.getProductName()));
                // insert..
                allSearchDatasMap.put(productId, tmpSD);
            }
            newSearchDataMap.clear();
        }
    }

    public static void main(String args[]) throws Exception {
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl;
        CrawlSite crawlSite = new CrawlSite();
        GlobalUtils globalUtils = new GlobalUtils();
        int index=0;

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.camping-plus.co.kr","http://www.camping-plus.co.kr");
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setCrawlEncode("euc-kr");
        crawlSite.setCrawlUrl("http://www.camping-plus.co.kr/mall/m_mall_list.php?ps_ctid=03030000&page=1");
        int returnCode = crawlSite.HttpCrawlGetDataTimeout();
        logger.info(String.format(" Return code (%d)", returnCode));
        String htmlContent = crawlSite.getCrawlData();

//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("table[width=\"130\"]");
        for (Element element : elements) {
            document = Jsoup.parse(element.outerHtml());

            if (!element.outerHtml().contains("padding-top:8px;text-align:center"))
                continue;

//            logger.info(element.outerHtml());
//            logger.info("----------------------------------------------------");

            // Thumb link
            listE = document.select("div[style=\"border:1px solid #D2D2D2;margin:0 0 5px 0;padding:2px\"] img");
            for (Element et : listE) {
                strItem = et.attr("src");
                if (strItem.contains("./shop_image")) {
                    logger.info(String.format("[%d]%s", index, prefixHostThumbUrl + strItem.replace("./shop_image", "/mall/shop_image")));
                } else {
                    logger.info(String.format("[%d]%s", index, prefixHostThumbUrl + strItem));
                }
                break;
            }

            // link
            listE = document.select("div[style=\"border:1px solid #D2D2D2;margin:0 0 5px 0;padding:2px\"] a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "ps_goid=");
                    logger.info(String.format("[%s]%s", productId, prefixContentUrl + productId));
                    break;
                }
            }

            // product name
            listE = document.select("div.goods_name_");
            for (Element et : listE) {
                strItem = et.text().trim();
                logger.info(String.format(" title :(%s) ", strItem));
            }

            // org price
            listE = document.select("div.market_price_ strike");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                    break;
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

            // sale price
            listE = document.select("div.price_");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> sale price(%s)", strItem));
                    break;
                } else {
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
                }
            }

            index++;
            logger.info("=======================================================================");
        }
    }
}
