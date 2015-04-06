package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.extract.ExtractInfo;
import com.erpy.io.FileIO;
import com.erpy.utils.*;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class First {
    private static Logger logger = Logger.getLogger(First.class.getName());
    private CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();
    private ValidChecker validChecker = new ValidChecker();
    private DB db = new DB();
    // for extract.
    private int totalExtractCount=0;
    private int skipCount=0;
    private int insertCount=0;
    private int updateCount=0;
    private int unknownCount=0;
    private static final String pattern = "div.list_01 span.sub_img";
    // for crawling.
    private int crawlCount=0;
    private int crawlErrorCount=0;
    private int collisionFileCount=0;

    private String filePath;
    private String keyword;
    private String txtEncode="utf-8";
    private String seedUrl;

    //
    private static final String prefixContentUrl = "http://www.chocammall.co.kr/shop/base/product/viewProductDetail.do?goods_no=";
    private static final String prefixHost = "http://www.chocammall.co.kr";

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
        Elements elementsLink;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        Document docu;
        String strLinkUrl=null;


        fileIO.setEncoding(txtEncode);
        fileIO.setPath(filePath);

        logger.debug(String.format(" 데이터 추출할 파일 - %s", filePath));

        ////////////////////////////////////////////////////////
        if (filePath==null) {
            logger.fatal(" FilePath is null !!");
            System.exit(-1);
        }

        // 분석할 파일을 하나 읽어 온다.
        String htmlContent = fileIO.getFileContent();

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("li");
        for (Element element : elements) {

            productId=null;
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());


            // Link
            listE = document.select("span.sub_img");
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select("a");
                for (Element elink : elementsLink) {
                    strItem = elink.attr("href");
                    strLinkUrl = strItem; // Used map key.
                    logger.debug(String.format(" >> Link : %s", strItem));
                }
                // extract productID
                productId = globalUtils.getFieldData(strLinkUrl, "viewProductDetail(", ")").trim();
                searchData.setContentUrl(prefixContentUrl + productId);
                logger.debug(String.format(" >> Link : %s", prefixContentUrl + productId));
                searchData.setProductId(productId);
            }

            // Thumb link
            listE = document.select("span.sub_img a");
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select("img ");
                for (Element elink : elementsLink) {
                    strItem = elink.attr("src");
                    searchData.setThumbUrl(prefixHost + strItem.replace("_2.jpg","_0.jpg"));
                    logger.debug(String.format(" >> Thumb : %s", prefixHost + strItem));
                    // 큰 이미지 : http://www.chocammall.co.kr/resources/product_image/201403/20140304_113005_0.jpg
                    // 작은 이미지 : http://www.chocammall.co.kr/resources/product_image/201403/20140304_113005_2.jpg
                }
            }

            // product name
            listE = document.select("li span.product_name");
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select("a");
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("\"", " ").replace("'", " ");
                    logger.debug(String.format(" >> title(%s)", strItem));
                    searchData.setProductName(strItem);
                }
            }

            // org price
            listE = document.select("li");
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select("span.sale_price");
                for (Element elink : elementsLink) {
                    strItem = elink.text().trim();
                    strItem = strItem.replace("원","").replace(",", "");
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
            }

            // cp name
            searchData.setCpName(GlobalInfo.CP_FIRST);
            // keyword
            searchData.setCrawlKeyword(globalUtils.isSexKeywordAdd(keyword, false, false));
            // sale price가 없을경우 org price 값을 넣어준다.
            searchData.setSalePrice(searchData.getOrgPrice());
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


    public String makeUrlPage(String url, int page)  {
        return String.format("%s&page=%d", url, page);
    }


    public void crawlData(String url, String strKeyword, String strCpName) throws IOException {
        Random random = new Random();
        DateInfo dateInfo = new DateInfo();
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        CrawlData crawlData = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int returnCode;
        long randomNum;
        int data_size;
        boolean lastPage=false;
        String strUrl;
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(5000);
        crawlSite.setCrawlEncode(txtEncode);

        for(;;) {

            // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
            strUrl = String.format("%s&page_no=%d", url, page);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);

            // Crawliing...
            try {
                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s", strUrl));
                    crawlErrorCount++;
                    continue;
                }
            }
            catch (Exception e) {
                logger.error(e.getStackTrace());
            }

            // 수집한 데이터를 파일로 저장한다.
            randomNum = random.nextInt(918277377);
            crawlSavePath = savePrefixPath + "/" + strCpName + "/" + Long.toString(randomNum) + ".html";

            // DIR check.
            File dir = new File(savePrefixPath + "/" + strCpName);
            if (!dir.exists()) {
                logger.info(" make directory : " + dir);
                dir.mkdir();
            }

            // 만일 파일이름이 충돌 난다면...
            File f = new File(crawlSavePath);
            if(f.exists()) {
                logger.error(String.format(" 저장할 파일 이름이 충돌 납니다 - %s ", crawlSavePath));
                collisionFileCount++;
                continue;
            }

            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            // 크롤링한 데이터를 파일로 저장한다.
            crawlIO.executeSaveData();

            // 추출된 데이터가 없으면 page 증가를 엄추고 새로운 seed로 다시 수집하기 위해
            // 추출된 데이터가 있는지 체크한다.
            data_size = globalUtils.checkDataCount(crawlSavePath, pattern, txtEncode);
            if (data_size < 30) {
                logger.info(String.format(" Data size is(%d). This seed last page : %s",data_size, strUrl));
                lastPage = true;
            }

            // 수집한 메타 데이터를 DB에 저장한다.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            // 크롤링한 메타데이터를 db에 저장한다.
            crawlDataService.insertCrawlData(crawlData);
            logger.info(String.format(" Crawling ( %d ) %s", data_size, strUrl));

            // page를 증가 시킨다.
            page++;
            // 크롤링한 데이터 카운트.
            crawlCount++;
            // 마지막 페이지이면 끝내고.
            if (lastPage) break;
            // page가 100페이지이면 끝난다. 100페이지까지 갈리가 없음.
            if (page==100) break;
        }
    }


    public void printResultInfo(First cp) {
        logger.info(" ================== Extracting information ==================");
        logger.info(String.format(" Total extract count - %d", cp.getTotalExtractCount()));
        logger.info(String.format(" Skip count          - %d", cp.getSkipCount()));
        logger.info(String.format(" Insert count        - %d", cp.getInsertCount()));
        logger.info(String.format(" Update count        - %d", cp.getUpdateCount()));
        logger.info(String.format(" Unknoun count       - %d", cp.getUnknownCount()));
        logger.info(" Extract processing terminated normally!!");
    }


    public void mainExtractProcessing(First cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap;
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
