package com.erpy.crawler;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by baeonejune on 14. 11. 30..
 */
public class CrawlIO {
    private String data;
    private String path;
    private String crawlEncoding = "euc-kr";
    private String saveEncoding = "utf-8";
    private String pattern=null;
    private String pageType="page";
    private String extractType="html";
    private int extractDataCount=0;

    private static final int MAX_PAGE = 11;
    private static final int MAX_COUPANG_PAGE = 377;
    private static final int MAX_OKMALL_PAGE = 50;
    private static final int MAX_CRAWL_ERROR_COUNT=3;
    private int crawlErrorCount=0;
    private int crawledCount=0;

    private static Logger logger = Logger.getLogger(CrawlIO.class.getName());
    private CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();
    private Map<String, String> httpReqHeader;


    public void setSaveDataInfo(String saveData, String saveFilePath, String encoding) {
        this.data = saveData;
        this.path = saveFilePath;
        this.crawlEncoding = encoding;
    }


    public void setCrawlIO(String pageType, int extractDataCount, String crawlEncoding, String saveEncoding, String countExtPattern) {
        this.pageType = pageType;
        this.extractDataCount = extractDataCount;
        this.crawlEncoding = crawlEncoding;
        this.saveEncoding = saveEncoding;
        this.pattern = countExtPattern;
    }

    public Map<String, String> getHttpReqHeader() {
        return httpReqHeader;
    }

    public void setHttpReqHeader(Map<String, String> httpReqHeader) {
        this.httpReqHeader = httpReqHeader;
    }

    public static int getMAX_PAGE() {
        return MAX_PAGE;
    }

    public void setSaveEncoding(String saveEncoding) {
        this.saveEncoding = saveEncoding;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getCrawlErrorCount() {
        return crawlErrorCount;
    }

    public int getCrawledCount() {
        return crawledCount;
    }

    public void setCrawlEncoding(String crawlEncoding) {
        this.crawlEncoding = crawlEncoding;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public void setExtractDataCount(int extractDataCount) {
        this.extractDataCount = extractDataCount;
    }

    public String getExtractType() {
        return extractType;
    }

    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }

    public boolean executeSaveData() throws IOException {
        if (path.length() <= 0) {
            logger.error(String.format(" file path is NOT - (%s)", path));
            return false;
        }

        File file = new File(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), saveEncoding));
        bw.write(data);
        bw.close();

        return true;
    }


    public boolean isSameCrawlData(Map<String, CrawlData> allCrawlDatasMap, String key) throws Exception {
//        logger.info(String.format(" key(%s)", key));
        if (allCrawlDatasMap.containsKey(key)) return true;
        return false;
    }


    public boolean isSaveFilePathCollision(String filePath) throws Exception {
        File f = new File(filePath);
        if(f.exists()) {
            logger.error(String.format(" 저장할 파일 이름이 충돌 납니다 - (%s) ", filePath));
            return false;
        }
        return true;
    }


    public boolean saveDirCheck(String savePrefixPath, String cpName) throws Exception {
        File dir;
        try {
            if (cpName.length()>0) {
                dir = new File(savePrefixPath + "/" + cpName);
            } else {
                dir = new File(savePrefixPath);
            }

            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    logger.error(String.format(" Make directory - (%s)", savePrefixPath));
                    return false;
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getStackTrace());
            return false;
        }
        return true;
    }


    public String makeSaveFilePath(String savePrefixPath, String cpName, Integer randomNumber) throws Exception {
        if (!saveDirCheck(savePrefixPath, cpName)) {
            logger.error(String.format(" Don't make [%s] directory !!", cpName));
            return "";
        }
        return savePrefixPath + "/" + cpName + "/" + Integer.toString(randomNumber) + ".html";
    }


    public String flushDiskCrawlData(String prefixPath, String cpName, Integer randomNumber,
                                      CrawlSite crawlSite, String saveEncoding) throws Exception {
        String savePath = makeSaveFilePath(prefixPath, cpName, randomNumber);
        if (savePath.length()==0) {
            logger.error(" Flush file path is NOT !!");
            return "";
        }
        if (!isSaveFilePathCollision(savePath)) {
            logger.error(String.format(" Crawling save file path [%s] is COLLISION !!", savePath));
            return "";
        }
        setSaveDataInfo(crawlSite.getCrawlData(), savePath, saveEncoding);
        executeSaveData();
        return savePath;
    }


    /////////////////////////////////////////////////////////////////////////////////
    public void crawlOne(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int offset=0;
        int returnCode;
        int data_size;
        int crawlErrorCount=0;
        boolean isLastPage=false;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);
        crawlSite.setRequestHeader(httpReqHeader);

        for(;;) {
            // make crawling url.
            strUrl = crawlIO.makeNextPageUrl(seed.getCpName(), seed.getUrl(), pageType, page, offset);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);

            try {
                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s", strUrl));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!", crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            /////////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same !!");
                break;
            }

            /////////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;
            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                page++;
                continue;
            }

            // 크롤링된 데이터를 disk 에 저장한다.
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            // 수집한 메타 데이터를 DB에 저장한다.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            // 크롤링한 메타데이터를 db에 저장한다.
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;
        }
    }


    //////////////////////////////////////////////////////////////////////////
    public void crawl(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();


        int page=1;
        int offset=0;
        int returnCode;
        int data_size=0;
        int crawlErrorCount=0;
        boolean isLastPage=false;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();
        String strHHmmss;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);
        crawlSite.setRequestHeader(httpReqHeader);

        for(;;) {
            // make crawling url.
            strUrl = crawlIO.makeNextPageUrl(seed.getCpName(), seed.getUrl(), pageType, page, offset);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);

            try {
                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, strUrl));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!", crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            ////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            ////////////////////////////////////////////////////////
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same !!");
                break;
            }

            /////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            /////////////////////////////////////////////////////////
            if (extractType.equals("html")) {
                data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            } else if (extractType.equals("json")) {
                data_size = globalUtils.checkDataCountContentJson(crawlSite.getCrawlData(), pattern);
            }
            if (data_size==0) break;
            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                if (isLastPage) break; // for first
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            strHHmmss = sdf.format(date) + String.format("%d",page);
//            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), Integer.parseInt(strHHmmss), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCateName1(seed.getCateName1());
            crawlData.setCateName2(seed.getCateName2());
            crawlData.setCateName3(seed.getCateName3());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            if (isLastPage) break; // for first
            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            offset += 32; // for campI
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;
        }
    }


    //////////////////////////////////////////////////////////////////////////
    // backup
//    public void crawl(String url, String strKeyword, String strCpName,
//                      Map<String, CrawlData> allCrawlDatasMap) throws Exception {
//
//        Random random         = new Random();
//        DateInfo dateInfo     = new DateInfo();
//        CrawlIO crawlIO       = new CrawlIO();
//        CrawlSite crawlSite   = new CrawlSite();
//        CrawlData crawlData   = new CrawlData();
//        GlobalInfo globalInfo = new GlobalInfo();
//
//        int page=1;
//        int offset=0;
//        int returnCode;
//        int data_size=0;
//        int crawlErrorCount=0;
//        boolean isLastPage=false;
//        String strUrl="";
//        String md5HashCode;
//        String beforePageMD5hashCode="";
//        String crawlSavePath;
//        String savePrefixPath = globalInfo.getSaveFilePath();
//
//        // 환경 셋팅
//        crawlSite.setConnectionTimeout(5000);
//        crawlSite.setSocketTimeout(10000);
//        crawlSite.setCrawlEncode(crawlEncoding);
//        crawlSite.setRequestHeader(httpReqHeader);
//
//        for(;;) {
//            // make crawling url.
//            strUrl = crawlIO.makeNextPageUrl(strCpName, url, pageType, page, offset);
//
//            // set crawling information.
//            crawlSite.setCrawlUrl(strUrl);
//
//            try {
//                returnCode = crawlSite.HttpCrawlGetDataTimeout();
//                if (returnCode != 200 && returnCode != 201) {
//                    logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, strUrl));
//                    crawlErrorCount++;
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
//                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!", crawlErrorCount));
//                    break;
//                }
//                crawlErrorCount++;
//                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
//                continue;
//            }
//
//            ////////////////////////////////////////////////////////
//            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
//            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
//            ////////////////////////////////////////////////////////
//            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
//            if (md5HashCode.equals(beforePageMD5hashCode)) {
//                logger.info(" Before hash code same !!");
//                break;
//            }
//
//            /////////////////////////////////////////////////////////
//            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
//            /////////////////////////////////////////////////////////
//            if (extractType.equals("html")) {
//                data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
//            } else if (extractType.equals("json")) {
//                data_size = globalUtils.checkDataCountContentJson(crawlSite.getCrawlData(), pattern);
//            }
//            if (data_size==0) break;
//            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.
//
//            // 동일한 데이터가 있으면 next page로 이동한다.
//            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + strCpName)) {
//                if (isCrawlEnd(page, strCpName)) break;
//                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
//                if (isLastPage) break; // for first
//                page++;
//                continue;
//            }
//
//            /////////////////////////////////////////////////////////
//            // 크롤링된 데이터를 disk 에 저장한다.
//            /////////////////////////////////////////////////////////
//            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, strCpName, random.nextInt(918277377), crawlSite, saveEncoding);
//            if (crawlSavePath.length()==0) {
//                logger.error(" Crawling data flush disk error !!");
//                if (isCrawlEnd(page, strCpName)) break;
//                page++;
//                continue;
//            }
//
//            /////////////////////////////////////////////////////////
//            // 수집한 메타 데이터를 DB에 저장한다.
//            /////////////////////////////////////////////////////////
//            crawlData.setSeedUrl(strUrl);
//            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
//            crawlData.setSavePath(crawlSavePath);
//            crawlData.setCpName(strCpName);
//            crawlData.setCrawlKeyword(strKeyword);
//            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
//            /////////////////////////////////////////////////////////
//            // 크롤링한 메타데이터를 db에 저장한다.
//            /////////////////////////////////////////////////////////
//            crawlDataService.insertCrawlData(crawlData);
//            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
//            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));
//
//            if (isLastPage) break; // for first
//            crawledCount++; // 크롤링한 데이터 카운트.
//
//            if (isCrawlEnd(page, strCpName)) break; // page 종료 조건 확인
//            page++; // page 증가
//            offset += 32; // for campI
//            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
//            crawlErrorCount=0;
//        }
//    }


    /////////////////////////////////////////////////////////////////////////////
    // for Timon
    // http://m.ticketmonster.co.kr/deal?cat=shopping&subcat=shopping_electronic&filter=112385
    /////////////////////////////////////////////////////////////////////////////
    private Map<String, String> extractRequestParam(String param, Map<String, String> fieldTagMap) throws Exception {
        Map<String, String> requestParamMap = new HashMap<String, String>();

        for(Map.Entry<String, String> entry : fieldTagMap.entrySet()) {
            requestParamMap.put(entry.getKey(), globalUtils.getFieldData(param, entry.getKey() + "=", entry.getValue()));
//            logger.info(String.format(" Set request header name(%s), value(%s)", entry.getKey().trim(), entry.getValue().trim()));
        }

        return requestParamMap;
    }


    /////////////////////////////////////////////////////////////////////////////
    // for timon
    /////////////////////////////////////////////////////////////////////////////
    private Map<String, String> makePostRequestData(Map<String, String> nameValueMap) throws Exception {
        String field, value;
        Map<String, String> requestParamMap = new HashMap<String, String>();

        for(Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            field = entry.getKey().trim();
            value = entry.getValue().trim();

            if ("cat".equals(field)) {
                requestParamMap.put("cat", value);
            } else if ("subcat".equals(field)) {
                requestParamMap.put("sub_cat", value);
            } else if ("filter".equals(field)) {
                requestParamMap.put("cat_srl", value);
            } else {
                logger.error(" Request param is else nothing !!");
            }
        }

        return requestParamMap;
    }


    /////////////////////////////////////////////////////////////////////////////
    // for timon.
    /////////////////////////////////////////////////////////////////////////////
    public void crawlTimon(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int returnCode;
        int data_size;
        int crawlErrorCount=0;
        boolean isLastPage=false;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();
        Map<String, String> extractParamNameMap = new HashMap<String, String>();
        Map<String, String> requestPostParam;


        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);
        // set request header
        crawlSite.setRequestHeader(httpReqHeader);
        // set request param extract field name.
        // sample - http://m.ticketmonster.co.kr/deal?cat=shopping&subcat=shopping_electronic&filter=112385";
        extractParamNameMap.put("cat","&");
        extractParamNameMap.put("subcat","&");
        extractParamNameMap.put("filter","&");

        // 1. url 에서 extractParamNameMap에 정의된 field 데이터를 추출.
        // 2. 추출한 field  데이터를 request post data에 맞는 field 로 변경.
        requestPostParam = makePostRequestData(extractRequestParam(seed.getUrl(), extractParamNameMap));
        requestPostParam.put("order", "popular");

        // crawling url이 변하지 않기 때문에 위에 셋팅.
        crawlSite.setCrawlUrl("http://m.ticketmonster.co.kr/deal/getMoreDealList");

        for( ;; ) {
            // 3. 기존에 page 데이터를 제거.
            requestPostParam.remove("page");
            // 4. 새로 증가된 페이지 데이터를 추가.
            requestPostParam.put("page", String.valueOf(page));
            // 5. crawling 하기 위한 post data param을 http method로 넘긴다.
            crawlSite.setPostFormDataParam(requestPostParam);

            try {
                returnCode = crawlSite.HttpPostGet();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s", strUrl));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!",
                            crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            ////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            ////////////////////////////////////////////////////////
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same and break !!");
                break;
            }

            /////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            /////////////////////////////////////////////////////////
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s, page=%d) ", crawlSite.getCrawlUrl(), page));
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s, page=%d", data_size, crawlSite.getCrawlUrl(), page));

            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;

            // 이미 수집한 데이터를 수집하지 않기 위해 저장한다.
            allCrawlDatasMap.put(md5HashCode + seed.getCpName(), crawlData);
        }
    }


    /////////////////////////////////////////////////////////////////////////////
    // for GSDeal
    /////////////////////////////////////////////////////////////////////////////
    public void crawlGSDeal(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int returnCode;
        int data_size;
        int crawlErrorCount=0;
        boolean isLastPage=false;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();
        Map<String, String> extractParamNameMap = new HashMap<String, String>();
        Map<String, String> requestPostParam = new HashMap<String, String>();
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader("m.gsshop.com","http://m.gsshop.com");

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);

        // set request header
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());

        // crawling url이 변하지 않기 때문에 위에 셋팅.
        crawlSite.setCrawlUrl("http://m.gsshop.com/deal/dealListAjax.gs?lseq=");

        for( ;; ) {
            // 3. 기존에 page 데이터를 제거.
            requestPostParam.remove("pageIdx");
            // 4. 새로 증가된 페이지 데이터를 추가.
            requestPostParam.put("pageIdx", String.valueOf(page));
            // 5. crawling 하기 위한 post data param을 http method로 넘긴다.
            crawlSite.setPostFormDataParam(requestPostParam);

            try {
                returnCode = crawlSite.HttpPostGet();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s | HTTP return %d", strUrl, returnCode));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!",
                            crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            ////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            ////////////////////////////////////////////////////////
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same and break !!");
                break;
            }

            /////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            /////////////////////////////////////////////////////////
            data_size = globalUtils.checkDataCountContentJson(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s, page=%d) ", crawlSite.getCrawlUrl(), page));
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s, page=%d |%d|%s", data_size, crawlSite.getCrawlUrl(), page,
                    crawlData.getCrawlDate().length(), beforePageMD5hashCode));

            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;

            // 이미 수집한 데이터를 수집하지 않기 위해 저장한다.
            allCrawlDatasMap.put(md5HashCode + seed.getCpName(), crawlData);
        }
    }


    /////////////////////////////////////////////////////////////////////////////
    // for HappyVirus POST
    /////////////////////////////////////////////////////////////////////////////
    public void crawlHappyVirusPost(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int returnCode;
        int data_size;
        int crawlErrorCount=0;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();
        Map<String, String> requestPostParam = new HashMap<String, String>();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);

        // set request header
        crawlSite.setRequestHeader(httpReqHeader);

        // crawling url이 변하지 않기 때문에 위에 셋팅.
        crawlSite.setCrawlUrl(seed.getUrl());

        for( ;; ) {
            // 3. 기존에 page 데이터를 제거.
            requestPostParam.remove("page");
            // 4. 새로 증가된 페이지 데이터를 추가.
            requestPostParam.put("page", String.valueOf(page));
            // 5. crawling 하기 위한 post data param을 http method로 넘긴다.
            crawlSite.setPostFormDataParam(requestPostParam);

            try {
                returnCode = crawlSite.HttpPostGet();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s | HTTP return %d", strUrl, returnCode));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!",
                            crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            ////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            ////////////////////////////////////////////////////////
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same and break !!");
                break;
            }

            /////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            /////////////////////////////////////////////////////////
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s, page=%d) ", crawlSite.getCrawlUrl(), page));
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s, page=%d |%d|%s", data_size, crawlSite.getCrawlUrl(), page,
                    crawlData.getCrawlDate().length(), beforePageMD5hashCode));

            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;

            // 이미 수집한 데이터를 수집하지 않기 위해 저장한다.
            allCrawlDatasMap.put(md5HashCode + seed.getCpName(), crawlData);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    // for TotoOutdoor POST
    public void crawlTotoOutdoor(Seed seed, Map<String, CrawlData> allCrawlDatasMap) throws Exception {
        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();

        int page=1;
        int returnCode;
        int data_size;
        int crawlErrorCount=0;
        String strUrl="";
        String md5HashCode;
        String beforePageMD5hashCode="";
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();
        Map<String, String> requestPostParam = new HashMap<String, String>();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);

        // set request header
        crawlSite.setRequestHeader(httpReqHeader);

        // crawling url이 변하지 않기 때문에 위에 셋팅.
        crawlSite.setCrawlUrl(seed.getUrl());

        logger.info(String.format(" POST URL : %s", seed.getUrl()));

        // url에서 default parameter 추출
        String strCategory = globalUtils.getFieldData(seed.getUrl(), "/menu/", "?");
        requestPostParam.put("page_size", "80");
        requestPostParam.put("firstType", "3");
        requestPostParam.put("firstVal", strCategory);
        requestPostParam.put("filter_category3", strCategory);

        for( ;; ) {
            // 3. 기존에 page 데이터를 제거.
            requestPostParam.remove("page_no");
            // 4. 새로 증가된 페이지 데이터를 추가.
            requestPostParam.put("page_no", String.valueOf(page));
            // 5. crawling 하기 위한 post data param을 http method로 넘긴다.
            crawlSite.setPostFormDataParam(requestPostParam);

            try {
                returnCode = crawlSite.HttpPostGet();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s | HTTP return %d", strUrl, returnCode));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
                if (crawlErrorCount > MAX_CRAWL_ERROR_COUNT) {
                    logger.error(String.format(" Crawling timeout occured max crawling count[%d] overed & this url skip!!",
                            crawlErrorCount));
                    break;
                }
                crawlErrorCount++;
                logger.error(String.format(" Crawling timeout occured !! - Retry(%d)", crawlErrorCount));
                continue;
            }

            ////////////////////////////////////////////////////////
            // 이전 페이지 본문 해시값과 현재 페이지 본문 해시값이 동일하면 break;
            // page를 증가해도 내용이 달라진게 없다는 뜻이다.
            ////////////////////////////////////////////////////////
            md5HashCode = globalUtils.MD5(crawlSite.getCrawlData());
            if (md5HashCode.equals(beforePageMD5hashCode)) {
                logger.info(" Before hash code same and break !!");
                break;
            }

            /////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            /////////////////////////////////////////////////////////
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + seed.getCpName())) {
                if (isCrawlEnd(page, seed.getCpName())) break;
                logger.info(String.format(" Skip crawling data - (%s, page=%d) ", crawlSite.getCrawlUrl(), page));
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, seed.getCpName(), random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, seed.getCpName())) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(seed.getCpName());
            crawlData.setCrawlKeyword(seed.getKeyword());
            crawlData.setCateName1(seed.getCateName1());
            crawlData.setCateName2(seed.getCateName2());
            crawlData.setCateName3(seed.getCateName3());
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s, page=%d |%d|%s", data_size, crawlSite.getCrawlUrl(), page,
                    crawlData.getCrawlDate().length(), beforePageMD5hashCode));

            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, seed.getCpName())) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;

            // 이미 수집한 데이터를 수집하지 않기 위해 저장한다.
            allCrawlDatasMap.put(md5HashCode + seed.getCpName(), crawlData);
        }
    }


    // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
    // snowppeak 사이트가 offset을 사용한다. 그외 사이트는 상관 없음.
    public String makeNextPageUrl(String cpName, String crawlStartUrl, String pagingType, int page, int offset) {
        String nextUrl;
        if (cpName.equals(GlobalInfo.CP_CouPang)) {
            nextUrl = String.format("%s?%s=%d", crawlStartUrl, pagingType, page);
        } else if (cpName.equals(GlobalInfo.CP_WeMef) || cpName.equals(GlobalInfo.CP_Timon) ||
                cpName.equals(GlobalInfo.CP_LotteThanksDeal) || cpName.equals(GlobalInfo.CP_HappyVirusFirst) ||
                cpName.equals(GlobalInfo.CP_HappyDeals)) {
            nextUrl = String.format("%s", crawlStartUrl);
        } else if (cpName.equals(GlobalInfo.CP_G9)) {
            nextUrl = String.format("%s&%s=%d", crawlStartUrl, pagingType, page);
        } else if (cpName.equals(GlobalInfo.CP_HotKill)) {
                nextUrl = String.format("http://m.hnsmall.com/category/hotkill/goods/%d/10/0", page);
        } else {
            nextUrl = String.format("%s&%s=%d&offset=%d", crawlStartUrl, pagingType, page, offset);
        }
        return nextUrl;
    }


    public boolean isCrawlEnd(int page, String cpName) {
        if (cpName.equals(GlobalInfo.CP_CouPang) || cpName.equals(GlobalInfo.CP_G9) || cpName.equals(GlobalInfo.CP_GSDeal)) {
            if (page > MAX_COUPANG_PAGE) return true;
        } else if (cpName.equals(GlobalInfo.CP_OKMALL)) {
            if (page > MAX_OKMALL_PAGE) return true;
        } else if (cpName.equals(GlobalInfo.CP_Timon) || cpName.equals(GlobalInfo.CP_HotKill)) {
            if (page > MAX_COUPANG_PAGE) return true;
        } else {
            if (page > MAX_PAGE) return true;
        }
        return false;
    }


    public static void main(String[] args) throws Exception {
        CrawlIO crawlIO = new CrawlIO();
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> fieldParamMap = new HashMap<String, String>();
        String url = "http://m.ticketmonster.co.kr/deal?cat=shopping&subcat=shopping_electronic&filter=112385";

        fieldParamMap.put("cat", "&");
        fieldParamMap.put("subcat", "&");
        fieldParamMap.put("filter", "&");

        map = crawlIO.extractRequestParam(url, fieldParamMap);
        for(Map.Entry<String, String> entry : map.entrySet()) {
            logger.info(String.format(" Set request header --> %s::%s", entry.getKey().trim(), entry.getValue().trim()));
        }
    }
}
