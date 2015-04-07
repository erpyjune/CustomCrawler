package com.erpy.crawler;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
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


    public void crawling(String url, String strKeyword, String strCpName,
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
        boolean isLastPage=false;
        String strUrl;
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode(crawlEncoding);

        for(;;) {
            // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
            strUrl = String.format("%s&%s=%d&offset=%d", url, pageType, page, offset);
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
                logger.error(e.getStackTrace());
            }

            /////////////////////////////////////////////////////////////
            // 추출된 데이터가 없으면 마지막 페이지 더 이상 page 증가 없이 종료한다.
            data_size = globalUtils.checkDataCountContent(crawlSite.getCrawlData(), pattern);
            if (data_size==0) break;
            if (extractDataCount==30 && data_size < extractDataCount) isLastPage = true; // for first.

            // 동일한 데이터가 있으면 next page로 이동한다.
            if (crawlIO.isSameCrawlData(allCrawlDatasMap, globalUtils.MD5(crawlSite.getCrawlData()) + strCpName)) {
                if (page++ > MAX_PAGE) break;
                logger.info(String.format(" Skip crawling data - (%s) ", strUrl));
                if (isLastPage) break; // for first
                continue;
            }

            // 크롤링된 데이터를 disk 에 저장한다.
            crawlSavePath = crawlIO.flushDiskCrawlData(savePrefixPath, strCpName, random.nextInt(918277377), crawlSite, saveEncoding);
            if (crawlSavePath.length()==0) {
                logger.error(" Crawling data flush disk error !!");
                if (page++ > MAX_PAGE) break;
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
            logger.info(String.format(" Crawled ( %d ) %s", data_size, strUrl));

            // for 캠핑퍼스트
            if (isLastPage) break;
            // 크롤링한 데이터 카운트.
            crawledCount++;
            // page가 11 페이지이면 끝난다. 11 페이지까지 갈리가 없음.
            if (page++ > MAX_PAGE) break;
            // for campI
            offset += 32;
        }
    }
}
