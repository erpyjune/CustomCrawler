package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
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
 * Created by baeonejune on 15. 3. 30..
 */
public class CampingOn {
    private static Logger logger = Logger.getLogger(CampingOn.class.getName());
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
    private String txtEncode="euc-kr";
    private String seedUrl;

    //
    private static final String prefixContentUrl = "http://www.campingon.co.kr/shop/goods/goods_view.php?goodsno=";
    private static final String prefixHostThumbUrl = "http://www.campingon.co.kr";

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

    public Map<String, SearchData> extract() throws Exception {
        FileIO fileIO = new FileIO();
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl;


        fileIO.setEncoding("utf-8");
        fileIO.setPath(filePath);

        logger.debug(String.format(" 데이터 추출할 파일 - %s", filePath));

        ////////////////////////////////////////////////////////
        if (filePath==null) {
            logger.fatal(" FilePath is null !!");
            throw new Exception("Extract file path is null!!");
        }

        // 분석할 파일을 하나 읽어 온다.
        String htmlContent = fileIO.getFileContent();

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("td[width=\"20%\"]");
        for (Element element : elements) {

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("img[width=150]");
            for (Element et : listE) {
                strItem = et.attr("src").replace("..","/shop");
                searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                logger.debug(String.format(" >> Thumb : %s", prefixHostThumbUrl + strItem));
            }

            // link
            listE = document.select("div[style=\"padding:5px 0 0 0; font-size:11px;\"] a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                // extract productID
                productId = globalUtils.getFieldData(strLinkUrl, "php?goodsno=","&").trim();
                searchData.setContentUrl(prefixContentUrl + productId);
                logger.debug(String.format(" >> Link : %s", prefixContentUrl + productId));
                searchData.setProductId(productId);
            }

            // product name
            listE = document.select("div[style=\"padding:5px 0 0 0; font-size:11px;\"] a");
            for (Element et : listE) {
                strItem = et.text();
                logger.debug(String.format(" >> title(%s)", strItem));
                searchData.setProductName(strItem);
            }

            // org price
            listE = document.select("div span strike");
            for (Element et : listE) {
                strItem = et.text().trim().replace("원", "").replace(",", "");
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> price(%s)", strItem));
                    searchData.setOrgPrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                }
            }

            // sale price
            listE = document.select("div[style=\"padding-bottom:3px; font-size:11px; color:#bc0000;\"] b");
            for (Element et : listE) {
                strItem = et.text().trim().replace("원", "").replace(",", "");
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> price(%s)", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                }
            }

            if (searchData.getOrgPrice()==0 && searchData.getSalePrice()>0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }

            if (searchData.getSalePrice()==0 && searchData.getOrgPrice()>0) {
                searchData.setSalePrice(searchData.getOrgPrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_CAMPINGON);
            // set keyword.
            searchData.setCrawlKeyword(keyword);
            // set seed url
            searchData.setSeedUrl(seedUrl);

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


    public void mainExtractProcessing(CampingOn cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Map<String, SearchData> newSearchDataMap;

        cp.setFilePath(crawlData.getSavePath());
        cp.setKeyword(crawlData.getCrawlKeyword());
        cp.setSeedUrl(crawlData.getSeedUrl());

        // 데이터 추출.
        searchDataMap = cp.extract();
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
}
