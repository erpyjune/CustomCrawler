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
    private static CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();
    private ValidChecker validChecker = new ValidChecker();
    private DB db = new DB();
    // for extract.
    private int totalExtractCount=0;
    private int skipCount=0;
    private int insertCount=0;
    private int updateCount=0;
    private int unknownCount=0;
    private static final String pattern = "td[width=\"20%\"] a";
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


        fileIO.setEncoding(txtEncode);
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
                if (strItem.indexOf("_s.jsp")>0) {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem.replace("_s.jpg","_m.jpg"));
                } else {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                }
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
        int data_size;
        String strUrl;
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(txtEncode);

        for(;;) {
            // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
            strUrl = String.format("%s&page=%d", url, page);
            crawlSite.setCrawlUrl(strUrl);

            try {
                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음. HTTP RET [%d] - %s", returnCode, strUrl));
                    crawlErrorCount++;
                    continue;
                }
            }
            catch (Exception e) {
                logger.error(e.getStackTrace());
            }

            // cp 디렉토리가 없으면 생성한다.
            if (!globalUtils.saveDirCheck(savePrefixPath, strCpName)) {
                logger.error(" Crawling data save dir make check fail !!");
                continue;
            }

            // save file path가 충돌나면 continue 한다.
            crawlSavePath = globalUtils.makeSaveFilePath(savePrefixPath, strCpName, random.nextInt(918277377));
            if (!globalUtils.isSaveFilePathCollision(crawlSavePath)) {
                logger.error(" Crawling save file path is collision !!");
                continue;
            }

            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            // 크롤링한 데이터를 파일로 저장한다.
            crawlIO.executeSaveData();

            // 추출된 데이터가 없으면 page 증가를 엄추고 새로운 seed로 다시 수집하기 위해
            // 추출된 데이터가 있는지 체크한다.
            data_size = globalUtils.checkDataCount(crawlSavePath, pattern, txtEncode);
            if (data_size <= 0) {
                logger.info(String.format(" Data size is(%d). This seed last page : %s",data_size, strUrl));
                break;
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
            // page가 100페이지이면 끝난다. 100페이지까지 갈리가 없음.
            if (page==100) break;
        }
    }


    public void printResultInfo(CampingOn cp) {
        logger.info(" ================== Extracting information ==================");
        logger.info(String.format(" Total extract count - %d", cp.getTotalExtractCount()));
        logger.info(String.format(" Skip count          - %d", cp.getSkipCount()));
        logger.info(String.format(" Insert count        - %d", cp.getInsertCount()));
        logger.info(String.format(" Update count        - %d", cp.getUpdateCount()));
        logger.info(String.format(" Unknoun count       - %d", cp.getUnknownCount()));
        logger.info(" Extract processing terminated normally!!");
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
