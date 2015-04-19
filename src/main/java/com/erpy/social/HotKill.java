package com.erpy.social;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import com.erpy.utils.DB;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import com.erpy.utils.ValidChecker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.*;

/**
 * Created by baeonejune on 15. 4. 18..
 */
public class HotKill {
    private static Logger logger = Logger.getLogger(HotKill.class.getName());
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
        ObjectMapper objectMapper = new ObjectMapper();
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

        byte[] jsonData = htmlContent.replace("\n","").trim().getBytes();
        JsonNode rootNode = objectMapper.readTree(jsonData).path("goodsList");
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            SearchData searchData = new SearchData();
            JsonNode node = iter.next();

            searchData.setProductId(node.path("goodsCode").asText());
            searchData.setThumbUrl("http://image.hnsmall.com/images" + node.path("imageUrl").asText() + node.path("imageR").asText());
            searchData.setContentUrl("http://m.hnsmall.com/goods/view/" + node.path("goodsCode").asText());
            searchData.setProductName(node.path("goodsName").asText());
            searchData.setOrgPrice(node.path("orgPrice").asInt());
            searchData.setSalePrice(node.path("bestPrice").asInt());
            searchData.setSellCount(node.path("soldqty").asInt());

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

            //////////////////////////////////////////////
            // org price만 있을 경우 sale price에 값을 채운다.
            //////////////////////////////////////////////
            if (searchData.getOrgPrice()>0 && searchData.getSalePrice()==0) {
                searchData.setSalePrice(searchData.getOrgPrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_HotKill);
            // set keyword.
            searchData.setCrawlKeyword(keyword);
            // set seed url
            searchData.setSeedUrl(seedUrl);

            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!globalUtils.isDataEmpty(searchData)) {
                // key : product id
                searchDataMap.put(searchData.getProductId() + searchData.getCpName(), searchData);
                totalExtractCount++;
            } else {
                logger.error(" Data field empty checked !!");
            }
        }

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    /////////////////////////////////////////////////////////////////////////////////
    // wemef는 검색결과 본문내에 next page 부터 모든 데이터가 들어 있다.
    /////////////////////////////////////////////////////////////////////////////////
    private Map<String, SearchData> extractJsonData(String source) throws Exception {
        Map<String, SearchData> map = new HashMap<String, SearchData>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        byte[] jsonData = source.trim().getBytes();
        JsonNode rootNode = objectMapper.readTree(jsonData).path("goodsList");
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            SearchData searchData = new SearchData();
            JsonNode node = iter.next();

            searchData.setProductId(node.path("goodsName").asText());
            searchData.setThumbUrl("http://image.hnsmall.com/images" + node.path("imageUrl").asText() + node.path("imageR").asText());
            searchData.setContentUrl("http://m.hnsmall.com/goods/view/" + node.path("goodsCode").asText());
            searchData.setProductName(node.path("goodsName").asText());
            searchData.setOrgPrice(node.path("orgPrice").asInt());
            searchData.setSalePrice(node.path("bestPrice").asInt());
            searchData.setSellCount(node.path("soldqty").asInt());


            ////////////////////////////////////////////////////////////////////////////////
//            System.out.println("title      : " +node.path("goodsName").asText());
//            System.out.println("brand      : "+node.path("gddesc").asText());
//            System.out.println("start date : "+node.path("g9sdt").asText());
//            System.out.println("end   date : "+node.path("expireDate").asText());
//            System.out.println("keywords   : "+node.path("keywords"));
//            System.out.println("brand name : "+node.path("brandnm").asText());
//            System.out.println("org  price : "+node.path("orgPrice").asInt());
//            System.out.println("sale price : "+node.path("bestPrice").asInt());
//            System.out.println("sale per   : "+node.path("couponRate").asInt());
//            System.out.println("sell count : "+node.path("soldqty").asInt());
//            System.out.println("url        : "+"http://m.hnsmall.com/goods/view/" + node.path("goodsCode").asText());
//            System.out.println("thumb      : "+"http://image.hnsmall.com/images" + node.path("imageUrl").asText() + node.path("imageR").asText());
//            System.out.println("-----------------------------------------------------------");
        }
        return map;
    }


    public void mainExtractProcessing(HotKill cp,
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
        CrawlSite crawlSite = new CrawlSite();
        HotKill hotKill = new HotKill();

        crawlSite.setCrawlEncode("utf-8");
        crawlSite.setCrawlUrl("http://m.hnsmall.com/category/hotkill/goods/1/10/0");

        crawlSite.HttpCrawlGetDataTimeout();
        hotKill.extractJsonData(crawlSite.getCrawlData());
    }
}
