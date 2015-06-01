package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class CampingMall {
    private static Logger logger = Logger.getLogger(CampingMall.class.getName());
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
    private static final String pattern = "div[style=\"width:180px;height:50px;padding-top:3px;\"] a";
    // for crawling.
    private int crawlCount=0;
    private int crawlErrorCount=0;
    private int collisionFileCount=0;

    private String filePath;
    private String keyword;
    private String txtEncode="utf-8";
    private String seedUrl;
    //
    private static final String prefixContentUrl = "http://www.campingmall.co.kr/shop/goods/goods_view.php?&goodsno=";
    private static final String prefixHostThumbUrl = "http://www.campingmall.co.kr";


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

    public String getSeedUrl() {
        return seedUrl;
    }

    public void setSeedUrl(String seedUrl) {
        this.seedUrl = seedUrl;
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
        GlobalUtils globalUtils = new GlobalUtils();
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
            System.exit(-1);
        }

        // 분석할 파일을 하나 읽어 온다.
        String htmlContent = fileIO.getFileContent();

        // 데이터 parsing을 위해 jsoup 객체로 읽는다.
        Document doc = Jsoup.parse(htmlContent);

        // 파싱 시작.
        elements = doc.select("td[width=\"25%\"");
        for (Element element : elements) {

            productId="";
            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Thumb link
            listE = document.select("img[width=180]");
            for (Element et : listE) {
                strItem = et.attr("src").replace("..","/shop");
                searchData.setThumbUrl(prefixHostThumbUrl + strItem);
                logger.debug(String.format(" >> Thumb : %s", prefixHostThumbUrl + strItem));
            }

            // link
            listE = document.select("div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
            for (Element et : listE) {
                strLinkUrl = et.attr("href");
                // extract productID
                productId = globalUtils.getFieldData(strLinkUrl, "php?&goodsno=").trim();
                searchData.setContentUrl(prefixContentUrl + productId);
                logger.debug(String.format(" >> Link : %s", prefixContentUrl + productId));
                searchData.setProductId(productId);
            }

            // product name
            listE = document.select("div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
            for (Element et : listE) {
                strItem = et.text();
                logger.debug(String.format(" >> title(%s)", strItem));
                searchData.setProductName(strItem);
            }

            // org price
            listE = document.select("div[style=\"height:10px;\"] strike");
            for (Element et : listE) {
                    strItem = globalUtils.priceDataCleaner(et.text());
                    if (GlobalUtils.isAllDigitChar(strItem)) {
                        logger.debug(String.format(" >> sale price(%s)", strItem));
                        searchData.setOrgPrice(Integer.parseInt(strItem));
                        break;
                    } else {
                        // org price가 없는것은 에러 이다.
                        // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                        logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                    }
            }

            // sale price
            listE = document.select("div[style=\"color:#ff4e00;font-size:16px;width:180pxheight:18px;\"] b");
            for (Element et : listE) {
                strItem = globalUtils.priceDataCleaner(et.text());
                if (GlobalUtils.isAllDigitChar(strItem)) {
                    logger.debug(String.format(" >> sale price(%s)", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                } else {
                    // org price가 없는것은 에러 이다.
                    // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                    logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                }
            }

            if (searchData.getOrgPrice()==0 && searchData.getSalePrice()>0) {
                searchData.setOrgPrice(searchData.getSalePrice());
            }

            // set cp name.
            searchData.setCpName(GlobalInfo.CP_CAMPINGMALL);
            // set keyword.
            searchData.setCrawlKeyword(isSexKeywordAdd(keyword, false, false));
            // set seedUrl
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

    private String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }


    public void mainExtractProcessing(CampingMall cp, CrawlData crawlData,
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
        String prefixHostThumbUrl = "http://www.campingmall.co.kr";
        String referer = "http://www.campingmall.co.kr";
        String hostDomain = "www.campingmall.co.kr";

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
        // CP에 해당되는 모든 데이터를 돌면서 수행
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
            elements = doc.select("table[width=\"500\"]");
            for (Element element : elements) {
                if (!element.outerHtml().contains("id=\"objImg\""))
                    continue;
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("img");
                for (Element et : listE) {
                    strItem = et.attr("src");
                    searchData.setThumbUrlBig(prefixHostThumbUrl + strItem.replace("../data","/shop/data"));
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
                    Thread.sleep(2500);
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        CrawlSite crawl = new CrawlSite();
        CampingMall cp = new CampingMall();

        crawl.setCrawlUrl("http://www.campingmall.co.kr/shop/goods/goods_list.php?category=002004&page=1");
        crawl.setCrawlEncode("euc-kr");
        crawl.HttpCrawlGetDataTimeout();

        Document doc = Jsoup.parse(crawl.getCrawlData());
        Elements elements = doc.select("td[width=\"25%\"");
        for (Element element : elements) {

            Document document = Jsoup.parse(element.outerHtml());

            // thumb
            Elements elist = document.select("img[width=180]");
            for (Element eitem : elist) {
                System.out.println("thumb : " + eitem.attr("src").replace("..",""));
            }

            // link
            Elements elink = document.select("div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
            for (Element eitem : elink) {
                // link
                System.out.println("link : " + eitem.attr("href"));
            }

            // title
            elink = document.select("div[style=\"width:180px;height:50px;padding-top:3px;\"] a");
            for (Element eitem : elink) {
                System.out.println("title : " + eitem.text());
            }

            // org price
            Elements ePrice = document.select("div[style=\"height:10px;\"] strike");
            for (Element eitem : ePrice) {
                System.out.println("org price : " + eitem.text().replace(",",""));
            }

            // sale price
            Elements eSalePrice = document.select("div[style=\"color:#ff4e00;font-size:16px;width:180pxheight:18px;\"] b");
            for (Element eitem : eSalePrice) {
                System.out.println("sale price : " + eitem.text().replace(",",""));
                System.out.println("----------------------------------------------------------");
            }
        }

        logger.info(" end test !!");
    }
}
