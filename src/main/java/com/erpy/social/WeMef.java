package com.erpy.social;

import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import com.erpy.utils.DB;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import com.erpy.utils.ValidChecker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by baeonejune on 15. 4. 12..
 */
public class WeMef {
    private static Logger logger = Logger.getLogger(WeMef.class.getName());
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
    private static final String prefixContentUrl = "http://m.wemakeprice.com/m/deal/adeal/";
    private static final String prefixHostThumbUrl = "";

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
        String productName1="", productName2="";
        float salePer;


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

        //////////////////////////////////////////////
        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        //////////////////////////////////////////////
        Document doc = Jsoup.parse(htmlContent);

        //////////////////////////////////////////////
        // Start parsing.
        //////////////////////////////////////////////
        elements = doc.select("li");
        for (Element element : elements) {

            // 이 문구가 없으면 skip.
            if (!element.outerHtml().contains("<p class=\"dt\">")) continue;

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            //////////////////////////////////////////////
            // Thumb link
            //////////////////////////////////////////////
            listE = document.select("a.img p.pic img");
            for (Element et : listE) {
                strItem = et.attr("src");
                searchData.setThumbUrl(strItem);
                logger.debug(String.format(" >> Thumb : (%s)", searchData.getThumbUrl()));
            }

            //////////////////////////////////////////////
            // link
            //////////////////////////////////////////////
            listE = document.select("li a.img");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "adeal/");
                    searchData.setContentUrl(prefixContentUrl + productId);
                    searchData.setProductId(productId);
                    logger.debug(String.format(" >> Link : (%s)", searchData.getContentUrl()));
                }
            }

            //////////////////////////////////////////////
            // shipping
            //////////////////////////////////////////////
            listE = document.select("p.spec span.pu");
            for (Element et : listE) {
                searchData.setShippingHow(et.text().trim());
                logger.debug(String.format(" >> shipping (%s)", searchData.getShippingHow()));
            }

            //////////////////////////////////////////////
            // product name1
            //////////////////////////////////////////////
            listE = document.select("div.info p.dt");
            for (Element et : listE) {
                productName1 = et.text().trim();
//                logger.debug(String.format(" >> title1(%s)", productName1));
            }

            //////////////////////////////////////////////
            // product name2
            //////////////////////////////////////////////
            listE = document.select("div.info p.p1");
            for (Element et : listE) {
                productName2 = et.text().trim();
//                logger.debug(String.format(" >> title2(%s)", productName2));
            }

            searchData.setProductName(productName1 + " " + productName2);
            logger.debug(String.format(" >> product name (%s)", searchData.getProductName()));

            //////////////////////////////////////////////
            // org price
            //////////////////////////////////////////////
            listE = document.select("div.info p.oldp");
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

            //////////////////////////////////////////////
            // sale price
            //////////////////////////////////////////////
            listE = document.select("div.info p.newp");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    searchData.setSalePer(0.0F);
                    logger.debug(String.format(" >> sale price(%s)", searchData.getSalePrice()));
                    break;
                } else {
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [sale price] product name      - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [sale price] seed url          - (%s)", crawlData.getSeedUrl()));
                }
            }

            //////////////////////////////////////////////
            // sell count
            //////////////////////////////////////////////
            listE = document.select("div.info p.bought");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
                    searchData.setSellCount(Integer.parseInt(strItem));
                    logger.debug(String.format(" >> sell count(%s)", searchData.getSellCount()));
                    break;
                } else {
                    logger.error(String.format(" Extract [sell count] data is NOT valid - (%s)", strItem));
                    logger.error(String.format(" Extract [sell count] product name      - (%s)", searchData.getProductName()));
                    logger.error(String.format(" Extract [sell count] crawl url         - (%s)", crawlData.getSeedUrl()));
                }
            }

//            // sale per
//            listE = document.select("dd.discount-rate");
//            for (Element et : listE) {
//                strItem = globalUtils.priceDataCleaner(et.text());
//                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
//                    searchData.setSalePer(Float.parseFloat(strItem));
//                    logger.debug(String.format(" >> sale per(%s)", searchData.getSalePer()));
//                    break;
//                } else {
//                    logger.error(String.format(" Extract [sale per] data is NOT valid - (%s)", strItem));
//                    logger.error(String.format(" Extract [sale per] product name      - (%s)", searchData.getProductName()));
//                    logger.error(String.format(" Extract [sale per] seed url          - (%s)", crawlData.getSeedUrl()));
//                }
//            }

            //////////////////////////////////////////////
            // sale per
            //////////////////////////////////////////////
            if (searchData.getSalePer()==0 && searchData.getSalePrice()>0 && searchData.getOrgPrice()>0) {
                salePer = searchData.getSalePrice() / searchData.getOrgPrice() * 100;
                searchData.setSalePer(salePer);
            }

            //////////////////////////////////////////////
            // sale price만 있을 경우 org price에 값을 채운다.
            //////////////////////////////////////////////
            if (searchData.getOrgPrice()==0 && searchData.getSalePrice()>0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_WeMef);
            // set keyword.
            searchData.setCrawlKeyword(keyword);
            // set seed url
            searchData.setSeedUrl(seedUrl);

            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!globalUtils.isDataEmpty(searchData)) {
                // key : product id
                searchDataMap.put(productId + searchData.getCpName(), searchData);
                totalExtractCount++;
            } else {
                logger.error(" Data field empty checked !!");
            }
        }

        String pageDataAnalisys = globalUtils.getFieldData(htmlContent, "var d_l =", "var d_c_l_id").trim();
        logger.info(String.format(" pageDataAnalisys length : %d", pageDataAnalisys.length()));
        Map<String, SearchData> nextListMap;
        nextListMap = extractJsonData(pageDataAnalisys);

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    private Map<String, SearchData> extractJsonData(String source) throws Exception {
        Map<String, SearchData> map = new HashMap<String, SearchData>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

//        System.out.println("ERPY start");
//        System.out.println(source);
//        System.out.println("ERPY end");
//        System.exit(-1);

        byte[] jsonData = source.trim().getBytes();
        JsonNode rootNode = objectMapper.readTree(jsonData);
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            SearchData searchData = new SearchData();
            JsonNode node = iter.next();

//            searchData.setProductId(node.path("deal_id").asText() + "/" + node.path("location_id").asText());
//            searchData.setThumbUrl(node.path("img_deal_list2").asText());

            System.out.println(prefixContentUrl + node.path("deal_id").asText() + "/" + node.path("location_id").asText());
            System.out.println(node.path("deal_id").asText());
            System.out.println(node.path("location_id").asText());
            System.out.println("title 1 : "+node.path("main_name").asText());
            System.out.println("http://image.wemakeprice.com/" + node.path("img_deal_list2").asText());
            System.out.println("title 2 : " + node.path("line_summary").asText());
            System.out.println("price : " + node.path("price").asText());
            System.out.println("sale per : " + node.path("dc_rate").asText());
            System.out.println("org_price : "+node.path("price_org").asText());
            System.out.println("sell count : "+node.path("qty_saled").asText());
            System.out.println("ship_price_type : "+node.path("ship_price_type").asText());
            System.out.println("price_free_ship : "+node.path("price_free_ship").asText());
            System.out.println("courier_company : "+node.path("courier_company").asText());
            System.out.println("card_discount_comment : "+node.path("card_discount_comment").asText());
            System.out.println("update_time : "+node.path("update_time").asText());
            System.out.println("mobile_sticker : "+node.path("mobile_sticker").asText().replace("<span class=\"deliv2\">","").
                    replace("</span>","").replace("<span class=\"pu\">",""));
            System.out.println("===========================================");
        }
        return map;
    }


    public void mainExtractProcessing(WeMef cp,
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

        crawlSite.setCrawlEncode("utf-8");
        crawlSite.setCrawlUrl("http://m.wemakeprice.com/m/main/100200/100208");
        int returnCode = crawlSite.HttpCrawlGetDataTimeout();
        String htmlContent = crawlSite.getCrawlData();

//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("li");
        for (Element element : elements) {
            document = Jsoup.parse(element.outerHtml());
            if (!element.outerHtml().contains("<p class=\"dt\">")) continue;

            index++;
//            logger.info(element.outerHtml());

            // Thumb link
            listE = document.select("a.img p.pic img");
            for (Element et : listE) {
                strItem = et.attr("src");
                logger.info(strItem);
            }

            // link
            listE = document.select("li a.img");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "adeal/");
                    logger.info(" url : " + prefixContentUrl + productId);
                    break;
                }
            }

            // shipping
            listE = document.select("p.spec span.pu");
            for (Element et : listE) {
                strItem = et.text().trim();
                logger.info(" shipping : " + strItem);
            }

            // product name1
            listE = document.select("div.info p.dt");
            for (Element et : listE) {
                strItem = et.text().trim();
                logger.info(" title1 : " + strItem);
            }

            // product name2
            listE = document.select("div.info p.p1");
            for (Element et : listE) {
                strItem = et.text().trim();
                logger.info(" title2 : " + strItem);
            }

            // org price
            listE = document.select("div.info p.oldp");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                    break;
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

            // sale price
            listE = document.select("div.info p.newp");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> sale price(%s)", strItem));
                    break;
                } else {
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", strItem));
                }
            }

            // sell count
            listE = document.select("div.info p.bought");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> sell count(%s)", strItem));
                    break;
                } else {
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", strItem));
                }
            }

				logger.info("============================================================================");
            logger.info(String.format(" index : %d", index));
        }
    }
}
