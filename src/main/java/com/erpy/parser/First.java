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
import java.util.concurrent.ExecutionException;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class First {
    private static Logger logger = Logger.getLogger(First.class.getName());
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
        String prefixHostThumbUrl="http://www.chocammall.co.kr";
        String referer="http://www.chocammall.co.kr";
        String hostDomain = "www.chocammall.co.kr";

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
            elements = doc.select("div.img_area");
            for (Element element : elements) {
                if (!element.outerHtml().contains("style=\"width:400px; height:400px;\""))
                    continue;
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("a img[style=\"width:400px; height:400px;\"]");
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
}
