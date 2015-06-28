package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.*;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by baeonejune on 15. 3. 8..
 */
public class SB {
    private static Logger logger = Logger.getLogger(SB.class.getName());
    // for extract.
    private int totalExtractCount=0;
    private int skipCount=0;
    private int insertCount=0;
    private int updateCount=0;
    private int unknownCount=0;
    private static final String pattern = "p.title a";
    // for crawling.
    private int crawlCount=0;
    private int crawlErrorCount=0;
    private int collisionFileCount=0;

    private String filePath;
    private String keyword;
    private String txtEncode="utf-8";
    private String seedUrl;
    private CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();
    private ValidChecker validChecker = new ValidChecker();
    private CrawlIO crawlIO = new CrawlIO();
    private DB db = new DB();

    private static final String postRequestUrl = "http://sbclub.co.kr/search_brandproductlist.html";
    private static final String prefixContentUrl = "";
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
        GlobalUtils globalUtils = new GlobalUtils();
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;


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
        elements = doc.select("li");
        for (Element element : elements) {

//            if (!element.outerHtml().contains("target=\'_self\'")) {
//                continue;
//            }

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // thumb
            listE = document.select("p.img a img");
            for (Element et : listE) {
                strItem = et.attr("src");
//                logger.info(" thumb : " + strItem);
                searchData.setThumbUrl("http://sbclub.co.kr" + strItem);
            }

            // set link, productId
            listE = document.select("p.title a");
            for (Element et : listE) {
                strItem = et.attr("href");
//                logger.info(" link : " + strItem);

                // get key
                searchData.setProductId(globalUtils.getFieldData(strItem, "pid="));
                searchData.setContentUrl("http://sbclub.co.kr/" + strItem);
            }

            // product name
            listE = document.select("p.title a");
            for (Element et : listE) {
                strItem = globalUtils.htmlCleaner(et.textNodes().toString());
//                logger.info(" title : " + strItem);
                searchData.setProductName(strItem);
            }

            // set org price, sale price.
            listE = document.select("p.title a span");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
//                logger.info(" price : " + strItem);
                searchData.setOrgPrice(Integer.parseInt(strItem));
            }

            // set sale price
            searchData.setSalePrice(searchData.getOrgPrice());
            // org price = sale price
            searchData.setSalePrice(searchData.getOrgPrice());
            // sale per
            searchData.setSalePer(0.0F);
            // cp name
            searchData.setCpName("sbclub");
            // keyword
            searchData.setCrawlKeyword(isSexKeywordAdd(keyword, false, false));
            // set seed url
            searchData.setSeedUrl(seedUrl);
            // set hash
            // cate code
            searchData.setCateName1(crawlData.getCateName1());
            searchData.setCateName2(crawlData.getCateName2());
            searchData.setCateName3(crawlData.getCateName3());


//            logger.debug(" ******************************************");


            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!globalUtils.isDataEmpty(searchData)) {
                // key : product id
                searchDataMap.put(productId + searchData.getCpName(), searchData);
                totalExtractCount++;
            }
        }

        return searchDataMap;
    }


    private String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }


//    public int checkDataCount(String path, String readEncoding) throws IOException {
//        String patten = "p.title";
//        FileIO fileIO = new FileIO();
//        fileIO.setPath(path);
//        fileIO.setEncoding(readEncoding);
//
//        String data = fileIO.getFileContent();
//        Document doc = Jsoup.parse(data);
//        Elements elements = doc.select(patten);
//        return elements.size();
//    }


    public String makeUrlPage(String url, int page)  {
        return String.format("%s&page=%d", url, page);
    }


    // 수집한 html을 파일로 저장 한다.
    private String flushCrawlFile(String cpName, String gpath, CrawlSite cp, CrawlIO io) throws Exception {
        Random random = new Random();
        int randomNum = random.nextInt(918277377);
        String crawlSavePath = gpath + "/" + cpName + "/" + Long.toString(randomNum) + ".html";

        // cp 디렉토리 체크.
        File dir = new File(gpath + "/" + cpName);
        if (!dir.exists()) {
            logger.info(" make directory : " + dir);
            if (!dir.mkdir()) {
                logger.error(String.format(" Fail make dir (%s)", gpath + "/" + cpName));
            }
        }

        // 만일 파일이름이 충돌 난다면 ERROR.
        File f = new File(crawlSavePath);
        if(f.exists()) {
            logger.error(String.format(" 저장할 파일 이름이 충돌 납니다 - %s ", crawlSavePath));
            collisionFileCount++;
        }

        io.setSaveDataInfo(cp.getCrawlData(), crawlSavePath, txtEncode);
        io.executeSaveData();

        return crawlSavePath;
    }


    // 수집한 정보에 대한 메타데이터를 DB에 저장한다.
    // 다운로드한 파일 path 정보가 주요 정보이다.
    private void insertToDBcrawlMetaData(
            CrawlData crawlData, String crawlSavePath, Seed seed, String categoryId, int startPage, int endPage) {

        DateInfo dateInfo = new DateInfo();
        crawlData.setSeedUrl(String.format("%s?categoryid=%s&startnum=%d&endnum=%d",
                seed.getUrl(), categoryId, startPage, endPage));
        crawlData.setCrawlDate(dateInfo.getCurrDateTime());
        crawlData.setSavePath(crawlSavePath);
        crawlData.setCpName(seed.getCpName());
        crawlData.setCrawlKeyword(keyword);
        crawlData.setCateName1(seed.getCateName1());
        crawlData.setCateName2(seed.getCateName2());
        crawlData.setCateName3(seed.getCateName3());
        // 크롤링한 메타데이터를 db에 저장한다.
        crawlDataService.insertCrawlData(crawlData);
    }


    public void crawlData(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {
        CrawlSite crawlSite = new CrawlSite();
        CrawlData crawlData = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();
        crawlDataService = new CrawlDataService();
        Map<String, String> postRequestParamMap = new HashMap<String, String>();

        int startPage=1;
        int endPage=40;
        int pageSize=40;
        int data_size;
        boolean lastPage=false;
        String crawlSavePath;


        // crawling 기본 환경 셋팅.
        crawlSite.setConnectionTimeout(3000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode("UTF-8");
        crawlSite.setCrawlUrl(postRequestUrl);

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("sbclub.co.kr", "http://sbclub.co.kr");
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());

        // seed url에서 category id만 추출해서 post request 호출할때 사용한다.
        // seed url에 category id가 없으면 에러임.
        // url --> http://sbclub.co.kr/category01.html?categoryid=94202
        String categoryId = globalUtils.getFieldData(seed.getUrl(), "html?categoryid=");
        if (categoryId==null) {
            logger.error(" Caterory ID를 추출하지 못했습니다.");
            return;
        }

        for(;;) {
            // Request param setting.
            // page, category, start page, end page.
            postRequestParamMap.remove("startnum");
            postRequestParamMap.remove("endnum");

            postRequestParamMap.put("mode", "categorymain");
            postRequestParamMap.put("categoryid", categoryId);
            postRequestParamMap.put("startnum", String.valueOf(startPage));
            postRequestParamMap.put("endnum", String.valueOf(endPage));
            crawlSite.setPostFormDataParam(postRequestParamMap);
//            logger.info(String.format(" Crawling start(%d), end(%d), cate(%s)", startPage, endPage, categoryId));

            try {
                // go crawling.
                crawlSite.HttpPostGet();
                if (crawlSite.getReponseCode() != 200 && crawlSite.getReponseCode() != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s", seed.getUrl()));
                    crawlErrorCount++;
                    continue;
                }
            }
            catch (Exception e) {
                logger.error(e.getStackTrace());
                continue;
            }

            // 추출된 데이터가 없으면 page 증가를 엄추고 새로운 seed로 다시 수집하기 위해
            // 추출된 데이터가 있는지 체크한다.
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size == 0) {
//                logger.info(String.format(" Data size is(%d). start(%d), end(%d), cate(%s)", data_size, startPage, endPage, categoryId));
                break;
            }

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, globalUtils.MD5(crawlSite.getCrawlData()) + seed.getCpName())) {
                if ((startPage + pageSize) > 440) break; // 11 page 이상이면 break.
                startPage = startPage + pageSize;
                endPage   = endPage + pageSize;
                logger.info(String.format(" Skip crawling data - startPage(%d), endPage(%d)  ", startPage, endPage));
                continue;
            }

            // clear request param
            crawlSite.clearPostRequestParam();

            // 크롤링한 데이터를 저장
            crawlSavePath = flushCrawlFile(seed.getCpName(), globalInfo.getSaveFilePath(), crawlSite, crawlIO);

            // 수집한 메타 데이터를 DB에 저장한다.
            crawlData.setSeedUrl(String.format("%s?categoryid=%s&startnum=%d&endnum=%d", seed.getUrl(), categoryId, startPage, endPage));
            // set hash data
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));

            insertToDBcrawlMetaData(
                    crawlData, crawlSavePath, seed, categoryId, startPage, endPage);
            logger.info(String.format(" Crawled (%d) (%s) start(%d), end(%d), cate(%s)", data_size, seed.getCpName(), startPage, endPage, categoryId));

            // 크롤링한 데이터 카운트.
            crawlCount++;

            // page를 증가 시킨다.
            startPage = startPage + pageSize;
            endPage   = endPage + pageSize;
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


    public void mainExtractProcessing(SB cp,
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


    /////////////////////////////////////////////////////////////////
    // 상품정보 url의 본문 정보에서 큰 이미지를 download 한다.
    public void thumbnailProcessing(String cpName, boolean isAllData) throws Exception {
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnailData;
        int returnCode, crawlErrorCount, imageSaveErrorCount;
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        SearchData searchData;
        String strItem;
        String key, imageFileName;

        String localPath = "/Users/baeonejune/work/SummaryNode/images";
        String prefixHostThumbUrl="http://www.sbclub.co.kr";
        String hostDomain = "www.sbclub.co.kr";

        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(localPath, cpName);

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(hostDomain, prefixHostThumbUrl);
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode("utf-8");

        ////////////////////////////////////////////////////////////////////////
        Map<String, SearchData> searchDataMap;
        if (isAllData) {
            // cpName에 해당되는 모든 데이터를 로딩
            searchDataMap = globalUtils.getAllSearchDatasByCP(cpName);
        } else {
            // cpName에 해당되는 데이터중에 thumb_big_url 필드가 없는것만 로딩.
            searchDataMap = globalUtils.getAllSearchDatasByCPBigThumbFieldNULL(cpName);
        }

        SearchDataService searchDataService = new SearchDataService();
        for(Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

//            logger.info("id       : " + searchData.getProductId());
//            logger.info("cp_name  : " + searchData.getCpName());

            crawlSite.setCrawlUrl(searchData.getContentUrl());

            crawlErrorCount=0;

            for (;;) {
                try {
                    returnCode = crawlSite.HttpCrawlGetDataTimeout();
                    if (returnCode != 200 && returnCode != 201) {
                        logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (crawlErrorCount >= 3) break;
                    crawlErrorCount++;
                    logger.error(Arrays.toString(e.getStackTrace()));
                }
            }

            doc = Jsoup.parse(crawlSite.getCrawlData());
            elements = doc.select("div.view");
            for (Element element : elements) {
                if (!element.outerHtml().contains("/goods/"))
                    continue;
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("div img");
                for (Element et : listE) {
                    strItem = et.attr("src");
                    searchData.setThumbUrlBig(prefixHostThumbUrl + strItem);
//                    logger.info(prefixHostThumbUrl + strItem);
                }
                break;
            }

            imageSaveErrorCount=0;

            while(true) {
                try {
                    // thumb_url_big URL에서 파일이름을 추출.
                    imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                    // 본문에서 big 이미지를 download 한다.
                    globalUtils.saveDiskImgage(localPath, cpName, searchData.getThumbUrlBig(), imageFileName);

                    // 기존 thumbnail이 있는지 찾는다.
                    thumbnailData.setCpName(cpName);
                    thumbnailData.setProductId(searchData.getProductId());
                    thumbnailData.setBigThumbUrl(searchData.getThumbUrlBig());
                    dbThumbnailData = thumbnailDataService.getFindThumbnailData(thumbnailData);
                    if (dbThumbnailData==null || dbThumbnailData.getBigThumbUrl().trim().length()==0) {
                        thumbnailDataService.insertThumbnailData(thumbnailData);
                    } else {
                        if (isAllData) {
                            thumbnailDataService.updateThumbnailData(thumbnailData);
                        }
                    }
                    break;
                } catch (Exception e) {
                    if (imageSaveErrorCount > 3) break;
                    logger.error(String.format(" Download image (%s) faile (%s)",
                            searchData.getThumbUrlBig(), e.getStackTrace().toString()));
                    imageSaveErrorCount++;
                    Thread.sleep(1100);
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        CrawlSite crawl = new CrawlSite();
        GlobalUtils globalUtils = new GlobalUtils();

        // http://sbclub.co.kr/category01.html?categoryid=94201?categoryid=94201&startnum=41&endnum=80
        crawl.setCrawlUrl("http://sbclub.co.kr/search_brandproductlist.html");
        crawl.setCrawlEncode("utf-8");
        crawl.addPostRequestParam("mode", "categorymain");
        crawl.addPostRequestParam("categoryid", "94201");
        crawl.addPostRequestParam("startnum", "41");
        crawl.addPostRequestParam("endnum", "80");

        crawl.HttpPostGet();

//        logger.info(String.format(" crawl size (%d)", crawl.getCrawlData().length()));
//        logger.info(crawl.getCrawlData());

        int total=0;
        Document doc = Jsoup.parse(crawl.getCrawlData());
        Elements elements = doc.select("li");
        for (Element element : elements) {
            Document document = Jsoup.parse(element.outerHtml());
//            logger.info(" "+element.outerHtml());
//            logger.info(" =======================================");

            // thumb
            Elements elist = document.select("p.img a img");
            for (Element eitem : elist) {
                logger.info(" thumb : http://sbclub.co.kr" + eitem.attr("src"));
            }

            // link
            Elements elink = document.select("p.title a");
            for (Element eitem : elink) {
                String strItem = eitem.attr("href");
                String brandcode = globalUtils.getFieldData(strItem, "brandcode=", "&");
                String pid = globalUtils.getFieldData(strItem, "pid=");
                logger.info(" url : http://sbclub.co.kr/" + strItem);
            }

            // product name
            Elements listE = document.select("p.title a");
            for (Element et : listE) {
                String strItem = globalUtils.htmlCleaner(et.textNodes().toString());
                logger.info(" title : " + strItem);
            }

            // org price
            Elements ePrice = document.select("p.title a span");
            for (Element eitem : ePrice) {
                String strItem = globalUtils.priceDataCleaner(eitem.text());
                logger.info(String.format(" org price : %s", strItem));
            }

//            // sale price
//            Elements eSalePrice = document.select("div[style=\"color:#ff4e00;font-size:16px;width:180pxheight:18px;\"] b");
//            for (Element eitem : eSalePrice) {
//                logger.info(String.format("%s", eitem.text().replace(",","")));
//            }

            total++;
            logger.info(" ===========================================================");
        }

        logger.info(" Total : " + total);
    }
}
