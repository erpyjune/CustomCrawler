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
 * Created by baeonejune on 15. 3. 15..
 */
public class DICamping {
    private static Logger logger = Logger.getLogger(DICamping.class.getName());
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
    private static final String prefixContentUrl = "http://www.dicamping.co.kr";
    private static final String prefixHostThumbUrl = "http://www.dicamping.co.kr";


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

        /////////////////////////////////////////
        // 파싱 시작.
        elements = doc.select("dl.item-list2");
        for (Element element : elements) {

            productId=null;
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());


            // Thumb link
            listE = document.select("dt.thumb2 img");
            for (Element et : listE) {
                strItem = et.attr("src");
                if (strItem.contains("small")) {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem.replace("small", "big"));
                } else {
                    searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                }
                logger.debug(String.format(" >> Thumb (%s)", prefixHostThumbUrl + strItem));
                break;
            }

            // link
            listE = document.select("dt.thumb2 a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                // extract productID
                productId = globalUtils.getFieldData(strLinkUrl, "branduid=", "&").trim();
                searchData.setContentUrl(prefixContentUrl + strLinkUrl);
                logger.debug(String.format(" >> Link (%s)", prefixContentUrl + strLinkUrl));
                logger.debug(String.format(" >> PrdID (%s)", productId));
                searchData.setProductId(productId);
            }

            // product name
            listE = document.select("li.prd-name");
            for (Element et : listE) {
                strItem = et.text();
                logger.debug(String.format(" >> title (%s)", strItem));
                searchData.setProductName(strItem);
            }

            // org price
            listE = document.select("ul li strike");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> org price (%s)", strItem));
                    searchData.setOrgPrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                }
            }

            // sale price
            listE = document.select("li.prd-price2");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> sale price (%s)", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [sale price] data is NOT valid - %s", strItem));
                }
            }

            // 이벤트 할인 행사면 추출 필드가 달라진다.
            // 이벤트 할인일 경우 org price, sale price 추출 위치가 달라진다.
            // event의 경우 org price
            listE = document.select("li s");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> event org price (%s)", strItem));
                    searchData.setOrgPrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [event org price] data is NOT valid (%s)", strItem));
                }
            }

            // 이벤트 할인 행사면 추출 필드가 달라진다.
            // 이벤트 할인일 경우 org price, sale price 추출 위치가 달라진다.
            // event의 경우 org price
            listE = document.select("li font b");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> event sale price (%s)", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [event sale price] data is NOT valid (%s)", strItem));
                }
            }

            if (searchData.getOrgPrice()==0 && searchData.getSalePrice()>0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }
            if (searchData.getOrgPrice()>0 && searchData.getSalePrice()==0) {
                searchData.setSalePrice(searchData.getOrgPrice());
            }

            // set sale per
            searchData.setSalePer(0.0F);
            // set cp name.
            searchData.setCpName(GlobalInfo.CP_DICAMPING);
            // set keyword.
            searchData.setCrawlKeyword(isSexKeywordAdd(keyword, false, false));
            // set seedUrl
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
            }
        }

        logger.info(String.format(" %d 건의 데이터 추출 완료", searchDataMap.size()));

        return searchDataMap;
    }


    private String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }


    public void mainExtractProcessing(DICamping cp,
                                      CrawlData crawlData,
                                      Map<String, SearchData> allSearchDatasMap) throws Exception {

        Map<String, SearchData> searchDataMap;
        Map<String, SearchData> newSearchDataMap;

        cp.setFilePath(crawlData.getSavePath());
        cp.setKeyword(crawlData.getCrawlKeyword());
        cp.setSeedUrl(crawlData.getSeedUrl());

        /////////////////////////////
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
        String prefixHostThumbUrl="http://www.dicamping.co.kr";
        String referer="http://www.dicamping.co.kr";
        String hostDomain = "www.dicamping.co.kr";

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
            elements = doc.select("div.thumb");
            for (Element element : elements) {
                if (!element.outerHtml().contains("id=\"lens_img\""))
                    continue;
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("img.detail_image");
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
        Elements elements;
        Document document;
        String strItem;
        String productId;
        Elements listE;
        String strLinkUrl = null;
        CrawlSite crawlSite = new CrawlSite();
        GlobalUtils globalUtils = new GlobalUtils();
        int index = 0;

        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("www.dicamping.co.kr","http://www.dicamping.co.kr");
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setCrawlEncode("euc-kr");
        crawlSite.setConnectionTimeout(1000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlUrl("http://www.dicamping.co.kr/shop/shopbrand.html?xcode=018&mcode=001&type=Y");
        int returnCode = crawlSite.HttpCrawlGetDataTimeout();
        String htmlContent = crawlSite.getCrawlData();

//        logger.info(" return code : " + returnCode);
//        logger.info(crawlSite.getCrawlData());
//        logger.info(String.format(" crawl contents size : %d", crawlSite.getCrawlData().length()));

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("dl.item-list2");
        for (Element element : elements) {
            productId = "";
            document = Jsoup.parse(element.outerHtml());

//            logger.info(element.outerHtml());
//            logger.info("==============================================");

            // Thumb link
            listE = document.select("dt.thumb2 img");
            for (Element et : listE) {
                strItem = et.attr("src");
                if (strItem.contains("small")) {
                    logger.info(String.format("[%d]%s", index, prefixHostThumbUrl + strItem.replace("small", "big")));
                } else {
                    logger.info(String.format("[%d]%s", index, prefixHostThumbUrl + strItem));
                }
                break;
            }

            // link
            listE = document.select("dt.thumb2 a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                if (strLinkUrl.length() > 0) {
                    productId = globalUtils.getFieldData(strLinkUrl, "branduid=", "&");
                    logger.info(String.format("[%s]%s", productId, prefixContentUrl + strLinkUrl));
                    break;
                }
            }

            // product name
            listE = document.select("li.prd-name ");
            for (Element et : listE) {
                strItem = et.text();
                logger.info(String.format(" title :(%s) ", strItem));
            }

            // org price
            listE = document.select("ul li strike");
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
            listE = document.select("li.prd-price2");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.info(String.format(" >> sale price(%s)", strItem));
                    break;
                } else {
                    logger.error(String.format(" Extract [sale price] data is NOT valid - (%s)", strItem));
                }
            }

            index++;
            logger.info("=======================================================================");
        }
    }
}
