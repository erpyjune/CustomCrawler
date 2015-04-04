package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.io.FileIO;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
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
 * Created by baeonejune on 15. 4. 4..
 */
public class CampTown {
    private static Logger logger = Logger.getLogger(CampTown.class.getName());
    private GlobalUtils globalUtils = new GlobalUtils();
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
    private static CrawlDataService crawlDataService;
    //
    private static final String prefixContentUrl = "http://www.camptown.co.kr/goods/view?no=";
    private static final String prefixHostThumbUrl = "http://www.camptown.co.kr";


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
        String strLinkUrl=null;


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
                if (globalUtils.isAllDigitChar(strItem)) {
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
                if (globalUtils.isAllDigitChar(strItem)) {
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


    public void updateToDB(Map<String, SearchData> sdAll) throws IOException {
        SearchDataService searchDataService = new SearchDataService();
        SearchData sd;

        for(Map.Entry<String, SearchData> entry : sdAll.entrySet()) {
            //strUrlLink = entry.getKey();
            sd = entry.getValue();

            // insert or update 타입이 없는 경우
            if (sd.getType().isEmpty()) {
                logger.warn(String.format(" UPDATED(empty) : (%s)(%s)(%s)",
                        sd.getProductId(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                unknownCount++;
            }
            else if (sd.getType().equals("insert")) {

                logger.info(String.format(" INSERTED : %s|%f|%d|%d|%s|%s",
                        sd.getProductId(),
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                insertCount++;
            }
            else if (sd.getType().equals("update")) {

                logger.info(String.format(" UPDATED : %s|%f|%d|%d|%s|%s",
                        sd.getProductId(),
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.updateSearchData(sd);
                updateCount++;
            }
            else {
                logger.error(String.format(" data biz is not (inset or update) %d", sd.getDataId()));
            }
        }
    }


    public int checkDataCount(String path, String readEncoding) throws IOException {
        String patten = "td[width=\"20%\"] a";
        FileIO fileIO = new FileIO();
        fileIO.setPath(path);
        fileIO.setEncoding(readEncoding);

        String data = fileIO.getFileContent();
        Document doc = Jsoup.parse(data);
        Elements elements = doc.select(patten);
        return elements.size();
    }


    public Map<String, SearchData> checkSearchDataValid(
            Map<String, SearchData> allMap, Map<String, SearchData> partMap) throws Exception {

        String productId;
        SearchData searchDataPart;
        SearchData searchDataAll;
        Map<String, SearchData> newSearchDataMap = new HashMap<String, SearchData>();

        for(Map.Entry<String, SearchData> entry : partMap.entrySet()) {
            productId = entry.getKey();
            searchDataPart = entry.getValue();

            if (globalUtils.isDataEmpty(searchDataPart)) {
                logger.error(String.format(" Null 데이터가 있어서 skip 합니다 (%s)",
                        searchDataPart.getProductId()));
                continue;
            }

            // 기존 추출된 데이터가 이미 존재하는 경우.
            if (allMap.containsKey(productId)) {
                // 기존 데이터에서 하나 꺼내서.
                searchDataAll = allMap.get(productId);
                if (globalUtils.isDataEmpty(searchDataAll)) {
                    logger.error(String.format(" 기존 데이중에 Null 데이터가 있어서 skip 합니다. prdid(%s)",
                            searchDataAll.getProductId()));
                    continue;
                }

                // 동일한 데이터가 있는지 비교한다.
                if (searchDataAll.getSalePrice().equals(searchDataPart.getSalePrice())
                        && searchDataAll.getProductName().equals(searchDataPart.getProductName())
                        && searchDataAll.getOrgPrice().equals(searchDataPart.getOrgPrice())) {

                    skipCount++;

                    // 동일한 데이터가 있으면 아무것도 안한다.

//                    logger.info(String.format(" SAME DATA (%s)(%d)(%d)(%s)",
//                            searchDataAll.getProductId(),
//                            searchDataAll.getSalePrice(),
//                            searchDataAll.getOrgPrice(),
//                            searchDataAll.getProductName()));
                }
                // product id는 동일하지만 필드 값이 다른경우.
                else {
                    logger.info(String.format(" UPDATE SET (%s)(%f)(%d)(%d)(%s)(%s)",
                            searchDataPart.getProductId(),
                            searchDataPart.getSalePer(),
                            searchDataPart.getSalePrice(),
                            searchDataPart.getOrgPrice(),
                            searchDataPart.getProductName(),
                            searchDataPart.getContentUrl()));

                    searchDataPart.setType("update");
                    searchDataPart.setDataStatus("U");
                    // 변경된 데이터가 있기 때문에 해당 db에 업데이트를 하기 위해 id 셋팅.
                    searchDataPart.setDataId(searchDataAll.getDataId());
                    newSearchDataMap.put(productId, searchDataPart);
                }
            }
            // 동일한 product id가 없는 경우
            else {
                logger.info(String.format(" INSERT SET %s", searchDataPart.getProductId()));
                searchDataPart.setType("insert");
                searchDataPart.setDataStatus("I");
                newSearchDataMap.put(productId, searchDataPart);
            }
        }

        return newSearchDataMap;
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
        crawlDataService = new CrawlDataService();

        int page=1;
        int returnCode;
        long randomNum=0;
        int data_size=0;
        boolean lastPage=false;
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
                logger.error(String.format(" Crawling data save dir make check fail !!"));
                continue;
            }

            // save file path가 충돌나면 continue 한다.
            crawlSavePath = globalUtils.makeSaveFilePath(savePrefixPath, strCpName, random.nextInt(918277377));
            if (!globalUtils.isSaveFilePathCollision(crawlSavePath)) {
                logger.error(String.format(" Crawling save file path is collision !!"));
                continue;
            }

            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            // 크롤링한 데이터를 파일로 저장한다.
            crawlIO.executeSaveData();

            // 추출된 데이터가 없으면 page 증가를 엄추고 새로운 seed로 다시 수집하기 위해
            // 추출된 데이터가 있는지 체크한다.
            data_size = checkDataCount(crawlSavePath, txtEncode);
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


    public void mainExtractProcessing(CampTown cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Map<String, SearchData> newSearchDataMap;

        cp.setFilePath(crawlData.getSavePath());
        cp.setKeyword(crawlData.getCrawlKeyword());

        // 데이터 추출.
        searchDataMap = cp.extract();
        if (searchDataMap.size() <= 0) {
            logger.error(String.format(" 이 파일은 추출된 데이터가 없습니다 (%s)",crawlData.getSavePath()));
            return ;
        }

        // DB에 들어있는 데이터와 쇼핑몰에서 가져온 데이터를 비교한다.
        // 비교결과 update, insert할 데이터를 모아서 리턴 한다.
        newSearchDataMap = cp.checkSearchDataValid(allSearchDatasMap, searchDataMap);
        if (newSearchDataMap.size() <= 0) {
            logger.info(String.format(" 변경되거나 새로 생성된 상품 데이터가 없습니다 - %s", crawlData.getSavePath()));
        }
        else {
            // db에 추출한 데이터를 넣는다.
            cp.updateToDB(newSearchDataMap);

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

    //=============================================================
    public static void main(String args[]) throws Exception {
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl = null;
        CrawlSite crawlSite = new CrawlSite();
        GlobalUtils globalUtils = new GlobalUtils();
        int index=0;

        crawlSite.setCrawlUrl("http://www.camptown.co.kr/goods/catalog?code=0006");
        int returnCode = crawlSite.HttpCrawlGetDataTimeout();
        String htmlContent = crawlSite.getCrawlData();

//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("td[width=\"130\"]");
        for (Element element : elements) {
            productId = "";
            document = Jsoup.parse(element.outerHtml());

            index++;
//            logger.info(element.outerHtml());

            // Thumb link
            listE = document.select("table tr td span a img");
            for (Element et : listE) {
                strItem = et.attr("src");
                if (strItem.indexOf("list2.jpg") > 0) {
                    logger.info(prefixHostThumbUrl + strItem.replace("list2.jpg", "view.jpg"));
                } else {
                    logger.info(prefixHostThumbUrl + strItem);
                }
            }

            // link
            listE = document.select("table tr td span a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "goods/view?no=");
                    logger.info(" url : " + prefixContentUrl + productId);
                }
            }

            // product name
            listE = document.select("span[style=\"color:#333333;font-family:dotum;font-size:10pt;font-weight:normal;text-decoration:none;\"]");
            for (Element et : listE) {
                strItem = et.text();
                logger.info(" title : " + strItem);
            }

            // org price
            listE = document.select("span[style=\"color:#666666;font-weight:normal;text-decoration:line-through;\"]");
            for (Element et : listE) {
                strItem = et.text().replace("원", "").replace(",", "").trim();
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                    break;
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

            // sale price
            listE = document.select("span[style=\"color:#cc6666;font-weight:bold;text-decoration:none;\"]");
            for (Element et : listE) {
                strItem = et.text().replace("원", "").replace(",", "").trim();
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> sale price(%s)", strItem));
                    break;
                } else {
                    logger.error(String.format(" Extract [org price] data is NOT valid - (%s)", strItem));
                }
            }

            logger.info("=======================================================================");
            logger.info(String.format(" index : %d", index));
        }
    }
}
