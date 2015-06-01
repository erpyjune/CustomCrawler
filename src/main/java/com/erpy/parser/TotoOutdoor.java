package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.CrawlData;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 15. 5. 6..
 */
public class TotoOutdoor {
    private static Logger logger = Logger.getLogger("TotoOutdoor");
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
    private static final String prefixContentUrl = "http://www.totooutdoor.com";
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
        elements = doc.select("ul.newbox");
        for (Element element : elements) {

            if (!element.outerHtml().contains("/good/detail/"))
                continue;

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("li.newbox_b1 a img");
            for (Element et : listE) {
                strItem = et.attr("src");
                searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                logger.debug(String.format(" >> Thumb : (%s)", searchData.getThumbUrl()));
                break;
            }

            // link
            listE = document.select("li.newbox_b1 a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "/detail/");
                    searchData.setContentUrl(prefixContentUrl + strLinkUrl);
                    searchData.setProductId(productId);
                    logger.debug(String.format(" >> Link : (%s)", searchData.getContentUrl()));
                    break;
                }
            }

            // product name
            listE = document.select("li.best_t1 a");
            for (Element et : listE) {
                searchData.setProductName(et.text());
                logger.debug(String.format(" >> title(%s)", searchData.getProductName()));
                break;
            }

            // org price
            listE = document.select("p.best_t2");
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
//            listE = document.select("div[style=\"padding-bottom:3px\"] b");
//            for (Element et : listE) {
//                strItem = globalUtils.priceDataCleaner(et.text());
//                if (strItem.length()>0 && GlobalUtils.isAllDigitChar(strItem)) {
//                    searchData.setSalePrice(Integer.parseInt(strItem));
//                    searchData.setSalePer(0.0F);
//                    logger.debug(String.format(" >> sale price(%s)", searchData.getSalePrice()));
//                    break;
//                } else {
//                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
//                    logger.error(String.format(" Extract [sale price] product name      - (%s)", searchData.getProductName()));
//                    logger.error(String.format(" Extract [sale price] seed url          - (%s)", crawlData.getSeedUrl()));
//                }
//            }

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
            searchData.setCpName(GlobalInfo.CP_Totooutdoor);
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

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    public void mainExtractProcessing(TotoOutdoor cp,
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


    /////////////////////////////////////////////////////////////////
    // 상품정보 url의 본문 정보에서 큰 이미지를 download 한다.
    public void thumbnailProcessing(String cpName) throws Exception {
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
        String prefixHostThumbUrl = "";
        String referer = "http://www.totooutdoor.com";
        String hostDomain = "www.totooutdoor.com";

        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(localPath, cpName);

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(hostDomain, referer);
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode("utf-8");

        ////////////////////////////////////////////////////////////////////////
        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(cpName);
        SearchDataService searchDataService = new SearchDataService();
        for (Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

//            logger.info("id       : " + searchData.getProductId());
//            logger.info("cp_name  : " + searchData.getCpName());

            crawlSite.setCrawlUrl(searchData.getContentUrl());

            crawlErrorCount = 0;

            for (; ; ) {
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
            elements = doc.select("div.pic");
            for (Element element : elements) {
                if (!element.outerHtml().contains("src=\"http://img.totooutdoor.com/images"))
                    continue;
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("img");
                for (Element et : listE) {
                    strItem = et.attr("src");
                    searchData.setThumbUrlBig(prefixHostThumbUrl + strItem);
//                    logger.info(prefixHostThumbUrl + strItem);
                }
                break;
            }

            imageSaveErrorCount = 0;

            while (true) {
                try {
                    // thumb_url_big URL에서 파일이름을 추출.
                    imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                    // 본문에서 big 이미지를 download 한다.
                    globalUtils.saveDiskImgage(localPath, cpName, searchData.getThumbUrlBig(), imageFileName);
                    // download된 thumb_url_big 필드를 search table에 업데이트 한다.
                    searchDataService.updateSearchData(searchData);
//                    logger.info(String.format(" update (%s)", searchData.getThumbUrlBig()));
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


    /////////////////////////////////////////////////////////////////////////////
    ///
    public static void main(String args[]) throws Exception {
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl;
        CrawlSite crawlSite = new CrawlSite();
        GlobalUtils globalUtils = new GlobalUtils();
        int index=0, returnCode=0;
        boolean isHttpGET=false;


        if (isHttpGET) {
            HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.totooutdoor.com", "http://www.totooutdoor.com");
            crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
            crawlSite.setCrawlEncode("utf-8");
            crawlSite.setCrawlUrl("http://www.totooutdoor.com/menu/1163");
            returnCode = crawlSite.HttpCrawlGetDataTimeout();
        } else {
            HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.totooutdoor.com","http://www.totooutdoor.com");
            crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
            crawlSite.setCrawlEncode("utf-8");

            // 두번째 페이지 추출.
            Map<String, String> postDataParam = new HashMap<String, String>();
            postDataParam.put("page_no", "1");
            postDataParam.put("page_size", "80");
            postDataParam.put("firstType", "3");
            postDataParam.put("firstVal", "1107");
//            postDataParam.put("filter_category1", "1001");
//            postDataParam.put("filter_category2", "1009");
            postDataParam.put("filter_category3", "1107");
            crawlSite.setPostFormDataParam(postDataParam);
            crawlSite.setCrawlUrl("http://www.totooutdoor.com/menu/1107?sort=");
            crawlSite.HttpPostGet();
        }


        String htmlContent = crawlSite.getCrawlData();

        logger.info(crawlSite.getCrawlData());
        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

        Document doc = Jsoup.parse(htmlContent);

        elements = doc.select("ul[class*=newbox]");
        for (Element element : elements) {

            if (!element.outerHtml().contains("/good/detail/"))
                continue;

            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("li.newbox_b1 a img");
            for (Element et : listE) {
                strItem = et.attr("src");
                logger.info("thumb : " + prefixHostThumbUrl + strItem);
                break;
            }

            // link
            listE = document.select("li.newbox_b1 a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length()>0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "/detail/");
                    logger.info(" url : " + prefixContentUrl + strLinkUrl);
                    logger.info(" pid : " + productId);
                    break;
                }
            }

            // product name
            listE = document.select("li.best_t1 a");
            for (Element et : listE) {
                strItem = et.text();
                logger.info(" title : " + strItem);
                break;
            }

            // org price
            listE = document.select("p.best_t2");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> org price(%s)", strItem));
                    break;
                } else {
                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
                }
            }

//            // sale price
//            listE = document.select("div[style=\"padding-bottom:3px\"] b");
//            for (Element et : listE) {
//                strItem = et.text().replace("원", "").replace(",", "").trim();
//                if (GlobalUtils.isAllDigitChar(strItem)) {
//                    logger.info(String.format(" >> org price(%s)", strItem));
//                } else {
//                    logger.info(String.format(" Extract [org price] data is NOT valid --> (%s)", strItem));
//                }
//            }

            logger.info(String.format("[%d]=======================================================================",++index));
        }
    }
}
