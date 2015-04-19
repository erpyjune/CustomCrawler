package com.erpy.social;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import com.erpy.utils.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * Created by baeonejune on 15. 4. 18..
 */
public class GSdeal {
    private static Logger logger = Logger.getLogger(Coopang.class.getName());
    private ValidChecker validChecker = new ValidChecker();
    private CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();
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
    private String encodingDataSave="utf-8";
    private String encodingCrawling="euc-kr";
    private String seedUrl;

    private String extractPattern="";

    //
    private static final String prefixContentUrl = "http://m.gsshop.com/deal/deal.gs?dealNo=";
    private static final String prefixHostThumbUrl = "http://image.gsshop.com/mi09/deal/dealno/";


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

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
    }

    public Map<String, SearchData> extract(CrawlData crawlData) throws Exception {
        FileIO fileIO = new FileIO();
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();


        if (filePath==null) {
            logger.fatal(" FilePath is null !!");
            throw new Exception("Extract file path is null!!");
        }

        fileIO.setEncoding(encodingDataSave);
        fileIO.setPath(filePath);

        String htmlContent;
        try {
            htmlContent = fileIO.getFileContent();
        } catch (Exception e) {
            logger.error(String.format(" File exist not - (%s)", filePath));
            return searchDataMap;
        }

        //////////////////////////////////////////////
        /// extract data.
        searchDataMap = extractJsonData(htmlContent);

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    /////////////////////////////////////////////////////////////////////////////////
    // GS Deal json 분리
    /////////////////////////////////////////////////////////////////////////////////
    private Map<String, SearchData> extractJsonData(String source) throws Exception {
        Map<String, SearchData> map = new HashMap<String, SearchData>();
        ObjectMapper objectMapper = new ObjectMapper();
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        String title1="", title2="";
        String productId="";

        byte[] jsonData = source.trim().getBytes();
        JsonNode rootNode = objectMapper.readTree(jsonData).get("list");
        Iterator<JsonNode> iter = rootNode.iterator();
        while(iter.hasNext()) {
            SearchData searchData = new SearchData();
            JsonNode node = iter.next();

            productId = node.path("dealNo").asText();
            searchData.setProductId(productId);
            searchData.setThumbUrl(prefixHostThumbUrl + node.path("bannrImg").asText());
            searchData.setContentUrl(prefixContentUrl + node.path("dealNo").asText());
            searchData.setShippingHow("");
            title1 = node.path("prdNm").asText();
            title2 = node.path("pmoNm").asText();
            searchData.setProductName(title1 + " " + title2);
            searchData.setOrgPrice(Integer.parseInt(globalUtils.priceDataCleaner(node.path("rowPrc").asText())));
            searchData.setSalePrice(Integer.parseInt(globalUtils.priceDataCleaner(node.path("rowPrc").asText())));
            searchData.setSellCount(Integer.parseInt(globalUtils.priceDataCleaner(node.path("ordQty").asText())));
            searchData.setSalePer(0F);
            // set cp name.
            searchData.setCpName(GlobalInfo.CP_GSDeal);
            // set keyword.
            searchData.setCrawlKeyword(keyword);
            // set seed url
            searchData.setSeedUrl(seedUrl);

            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!globalUtils.isDataEmpty(searchData)) {
                // key : product id
                map.put(productId + searchData.getCpName(), searchData);
                totalExtractCount++;
            } else {
                logger.error(" Extract data field empty checked !!");
            }

//            System.out.println("url       : http://m.gsshop.com/deal/deal.gs?dealNo="+node.path("dealNo").asText());
//            System.out.println("title      : "+node.path("prdNm").asText());
//            System.out.println("sub title  : "+node.path("pmoNm").asText());
//            System.out.println("sale price : "+node.path("rowPrc").asText());
//            System.out.println("thumb      : http://image.gsshop.com/mi09/deal/dealno/"+node.path("bannrImg").asText());
//            System.out.println("sell count : "+node.path("ordQty").asText());
//            System.out.println("end date   : "+node.path("endDtm").asText());
//            System.out.println("===========================================");
        }
        return map;
    }


    /////////////////////////////////////////////////////////////////
    public void mainExtractProcessing(GSdeal cp,
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
        Map<String, String> httpRequestParamMap = new HashMap<String, String>();
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl;
        GlobalUtils globalUtils = new GlobalUtils();
        int index=0;
        GSdeal gSdeal = new GSdeal();

        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.gsshop.com","http://m.gsshop.com");


        crawlSite.setConnectionTimeout(3000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode("euc-kr");
        crawlSite.setCrawlUrl("http://m.gsshop.com/deal/dealListAjax.gs?lseq=");
        httpRequestParamMap.put("pageIdx", "1");
        crawlSite.setPostFormDataParam(httpRequestParamMap);
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());

        crawlSite.HttpPostGet();
        crawlSite.getCrawlData();

        gSdeal.extractJsonData(crawlSite.getCrawlData());

//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

    }
}
