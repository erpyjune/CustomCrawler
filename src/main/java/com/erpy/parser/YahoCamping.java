package com.erpy.parser;

import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.CrawlData;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import com.erpy.utils.DB;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import com.erpy.utils.ValidChecker;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 15. 5. 7..
 */
public class YahoCamping {
    private static Logger logger = Logger.getLogger("YahoCamping");
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

    //
    private static final String prefixContentUrl = "http://www.yahocamping.com";
    private static final String prefixHostThumbUrl = "http://www.yahocamping.com";

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
        float salePer=0.0F;


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
        elements = doc.select("dl.item-list");
        for (Element element : elements) {

            if (!element.outerHtml().contains("<li class=\"prd-name\">"))
                continue;

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("dt img");
            for (Element et : listE) {
                strItem = et.attr("src");
                searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                logger.debug(String.format(" >> Thumb : (%s)", searchData.getThumbUrl()));
                break;
            }

            // link
            listE = document.select("dt a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "branduid=","&");
                    searchData.setContentUrl(prefixContentUrl + strLinkUrl);
                    searchData.setProductId(productId);
                    logger.debug(String.format(" >> Link : (%s)", searchData.getContentUrl()));
                    break;
                }
            }

            // product name
            listE = document.select("li.prd-name");
            for (Element et : listE) {
                searchData.setProductName(et.text());
                logger.debug(String.format(" >> title(%s)", searchData.getProductName()));
                break;
            }

            // org price
            listE = document.select("li[style=\"color:#FF0000; text-decoration:line-through;\"]");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setOrgPrice(Integer.parseInt(strItem));
                    logger.debug(String.format(" >> org price(%s)", searchData.getOrgPrice()));
                    break;
                } else {
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [org price] product name      - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [org price] seed url          - (%s)", crawlData.getSeedUrl()));
                }
            }

            // sale price
            listE = document.select("li.prd-price");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setOrgPrice(Integer.parseInt(strItem));
                    logger.debug(String.format(" >> sale price(%s)", searchData.getOrgPrice()));
                    break;
                } else {
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [sale price] product name      - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [sale price] seed url          - (%s)", crawlData.getSeedUrl()));
                }
            }

            // sale per
            if (searchData.getSalePer()==0 && searchData.getSalePrice()>0 && searchData.getOrgPrice()>0) {
                salePer = searchData.getSalePrice() / searchData.getOrgPrice() * 100;
                searchData.setSalePer(salePer);
            }

            // sale price만 있을 경우 org price에 값을 채운다.
            if (searchData.getOrgPrice()==0 && searchData.getSalePrice()>0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }

            // org price만 있을 경우 sale price에 값을 채운다.
            if (searchData.getOrgPrice()>0 && searchData.getSalePrice()==0) {
                searchData.setSalePrice(searchData.getOrgPrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_YahoCamping);
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
            } else {
                logger.error(" Data field empty checked !!");
            }
        }

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    public void mainExtractProcessing(YahoCamping cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap;
        Map<String, SearchData> newSearchDataMap;

        cp.setFilePath(crawlData.getSavePath());
        cp.setKeyword(crawlData.getCrawlKeyword());
        cp.setSeedUrl(crawlData.getSeedUrl());

        //////////////////////////////////////////////////////////////////////
        // 데이터 추출.
        //////////////////////////////////////////////////////////////////////
        searchDataMap = cp.extract(crawlData);
        if (searchDataMap.size() <= 0) {
            logger.error(String.format(" 이 파일은 추출된 데이터가 없습니다 (%s)",crawlData.getSavePath()));
            return ;
        }

        //////////////////////////////////////////////////////////////////////
        // DB에 들어있는 데이터와 쇼핑몰에서 가져온 데이터를 비교한다.
        // 비교결과 update, insert할 데이터를 모아서 리턴 한다.
        //////////////////////////////////////////////////////////////////////
        newSearchDataMap = validChecker.checkSearchDataValid(allSearchDatasMap, searchDataMap);
        if (newSearchDataMap.size() <= 0) {
            logger.info(String.format(" 변경되거나 새로 생성된 상품 데이터가 없습니다 - %s", crawlData.getSavePath()));
        }
        else {
            //////////////////////////////////////////////////////////////////////
            // db에 추출한 데이터를 넣는다.
            db.updateToDB(newSearchDataMap);

            //////////////////////////////////////////////////////////////////////
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

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.yahocamping.com","http://www.yahocamping.com");
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setCrawlEncode("euc-kr");
        crawlSite.setCrawlUrl("http://www.yahocamping.com/shop/shopbrand.html?xcode=051&type=N&mcode=010");
        int returnCode = crawlSite.HttpCrawlGetDataTimeout();
        String htmlContent = crawlSite.getCrawlData();

//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));


        Document doc = Jsoup.parse(htmlContent);

        elements = doc.select("dl.item-list");
        for (Element element : elements) {

            if (!element.outerHtml().contains("<li class=\"prd-name\">"))
                continue;

            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("dt img");
            for (Element et : listE) {
                strItem = et.attr("src");
                logger.info(prefixHostThumbUrl + strItem);
                break;
            }

            // link
            listE = document.select("dt a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "branduid=","&");
                    logger.info(" url : " + prefixContentUrl + strLinkUrl);
                    break;
                }
            }

            // product name
            listE = document.select("li.prd-name");
            for (Element et : listE) {
                strItem = et.text();
                logger.info(" title : " + strItem);
                break;
            }

            // org price
            listE = document.select("li[style=\"color:#FF0000; text-decoration:line-through;\"]");
            for (Element et : listE) {
                strItem = et.text().replace("원", "").replace(",", "").replace("￦","").trim();
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                    break;
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

            // sale price
            listE = document.select("li.prd-price");
            for (Element et : listE) {
                strItem = et.text().replace("원", "").replace(",", "").replace("￦","").trim();
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

            logger.info(String.format("[%d]=======================================================================",++index));
        }
    }
}
