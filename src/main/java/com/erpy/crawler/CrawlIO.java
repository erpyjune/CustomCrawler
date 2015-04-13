package com.erpy.crawler;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private int extractDataCount=0;

    private static final int MAX_PAGE = 11;
    private static final int MAX_COUPANG_PAGE = 511;
    private static final int MAX_OKMALL_PAGE = 50;
    private static final int MAX_CRAWL_ERROR_COUNT=3;
    private int crawlErrorCount=0;
    private int crawledCount=0;

    private static Logger logger = Logger.getLogger(CrawlIO.class.getName());
    private CrawlDataService crawlDataService = new CrawlDataService();
    private GlobalUtils globalUtils = new GlobalUtils();


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
        try {
            File dir = new File(savePrefixPath + "/" + cpName);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    logger.error(String.format(" mkdir - (%s)", savePrefixPath));
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


    public void crawlOne(String url, String strKeyword, String strCpName,
                          Map<String, CrawlData> allCrawlDatasMap) throws Exception {

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

        for(;;) {
            // wemef는 1번만 수집하면 된다.
            isLastPage = true;

            // make crawling url.
            strUrl = crawlIO.makeNextPageUrl(strCpName, url, pageType, page, offset);

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
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + strCpName)) {
                if (isCrawlEnd(page, strCpName)) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                if (isLastPage) break; // for first
                page++;
                continue;
            }

            // 크롤링된 데이터를 disk 에 저장한다.
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, strCpName, random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, strCpName)) break;
                page++;
                continue;
            }

            // 수집한 메타 데이터를 DB에 저장한다.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            // 크롤링한 메타데이터를 db에 저장한다.
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            if (isLastPage) break; // for first
            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, strCpName)) break; // page 종료 조건 확인
            page++; // page 증가
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;
        }
    }


    //////////////////////////////////////////////////////////////////////////
    public void crawl(String url, String strKeyword, String strCpName,
                      Map<String, CrawlData> allCrawlDatasMap) throws Exception {

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

        for(;;) {
            // make crawling url.
            strUrl = crawlIO.makeNextPageUrl(strCpName, url, pageType, page, offset);

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
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;
            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + strCpName)) {
                if (isCrawlEnd(page, strCpName)) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                if (isLastPage) break; // for first
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, strCpName, random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, strCpName)) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            if (isLastPage) break; // for first
            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, strCpName)) break; // page 종료 조건 확인
            page++; // page 증가
            offset += 32; // for campI
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;
        }
    }


    /////////////////////////////////////////////////////////////////////////////
    // make request header.
    /////////////////////////////////////////////////////////////////////////////
    private Map<String, String> makeRequestHeader() throws Exception {
        Map<String, String> requestHeaderMap = new HashMap<String, String>();

        requestHeaderMap.put("Host", "m.ticketmonster.co.kr");
        requestHeaderMap.put("Connection", "keep-alive");
        requestHeaderMap.put("Accept", "*/*");
        requestHeaderMap.put("Origin", "http://m.ticketmonster.co.kr");
        requestHeaderMap.put("X-Requested-With", "XMLHttpRequest");
        requestHeaderMap.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        requestHeaderMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        requestHeaderMap.put("Referer", "http://m.ticketmonster.co.kr/deal/?cat=fashion&subcat=fashion_female");
        requestHeaderMap.put("Accept-Encoding", "gzip, deflate");
        requestHeaderMap.put("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");

        return requestHeaderMap;
    }


    /////////////////////////////////////////////////////////////////////////////
    // for timon.
    /////////////////////////////////////////////////////////////////////////////
    public void crawlTimon(String url, String strKeyword, String strCpName,
                      Map<String, CrawlData> allCrawlDatasMap) throws Exception {

        Random random         = new Random();
        DateInfo dateInfo     = new DateInfo();
        CrawlIO crawlIO       = new CrawlIO();
        CrawlSite crawlSite   = new CrawlSite();
        CrawlData crawlData   = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();
        Map<String, String> requestHeaderMap=null;
        Map<String, String> requestParam=null;

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

        for(;;) {
            // make crawling url.
            strUrl = crawlIO.makeNextPageUrl(strCpName, url, pageType, page, offset);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);

            try {
//                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                returnCode = crawlSite.HttpPostGet();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" 데이터를 수집 못했음 - %s", strUrl));
                    crawlErrorCount++;
                }
            }
            catch (Exception e) {
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
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;
            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, md5HashCode + strCpName)) {
                if (isCrawlEnd(page, strCpName)) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                if (isLastPage) break; // for first
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 크롤링된 데이터를 disk 에 저장한다.
            /////////////////////////////////////////////////////////
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, strCpName, random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (isCrawlEnd(page, strCpName)) break;
                page++;
                continue;
            }

            /////////////////////////////////////////////////////////
            // 수집한 메타 데이터를 DB에 저장한다.
            /////////////////////////////////////////////////////////
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            crawlData.setHashMD5(globalUtils.MD5(crawlSite.getCrawlData()));
            /////////////////////////////////////////////////////////
            // 크롤링한 메타데이터를 db에 저장한다.
            /////////////////////////////////////////////////////////
            crawlDataService.insertCrawlData(crawlData);
            beforePageMD5hashCode = md5HashCode; // 이전 page 값과 현재 page hash 값이 동일한지 체크하기 위해 남긴다.
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            if (isLastPage) break; // for first
            crawledCount++; // 크롤링한 데이터 카운트.

            if (isCrawlEnd(page, strCpName)) break; // page 종료 조건 확인
            page++; // page 증가
            offset += 32; // for campI
            // 수집 완료를 했기 때문에 새로운 url은 timeout count를 0으로 초기화.
            crawlErrorCount=0;
        }
    }


    // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
    // snowppeak 사이트가 offset을 사용한다. 그외 사이트는 상관 없음.
    public String makeNextPageUrl(String cpName, String crawlStartUrl, String pagingType, int page, int offset) {
        String nextUrl;
        if (cpName.equals(GlobalInfo.CP_CooPang)) {
            nextUrl = String.format("%s?%s=%d", crawlStartUrl, pagingType, page);
        } else if (cpName.equals(GlobalInfo.CP_WeMef) || cpName.equals(GlobalInfo.CP_Timon)) {
            nextUrl = String.format("%s", crawlStartUrl);
        } else {
            nextUrl = String.format("%s&%s=%d&offset=%d", crawlStartUrl, pagingType, page, offset);
        }
        return nextUrl;
    }


    public boolean isCrawlEnd(int page, String cpName) {
        if (cpName.equals(GlobalInfo.CP_CooPang)) {
            if (page > MAX_COUPANG_PAGE) return true;
        } else if (cpName.equals(GlobalInfo.CP_OKMALL)) {
            if (page > MAX_OKMALL_PAGE) return true;
        } else {
            if (page > MAX_PAGE) return true;
        }
        return false;
    }
}
