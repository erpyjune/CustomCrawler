package com.erpy.thumbnail;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.*;
import com.erpy.utils.GlobalInfo;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public class ThumbnailProc {
    private static Logger logger = Logger.getLogger("ThumbnailProc");
    /////////////////////////////////////////////////////////////////
    // 상품정보 url의 본문 정보에서 큰 이미지를 download 한다.
    public void thumbnailProcessing(ThumbnailProcData thumbnailProcData) throws Exception {
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        int returnCode, crawlErrorCount, imageSaveErrorCount;
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnailData;
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        SearchData searchData;
        String strItem;
        String key, imageFileName;
        boolean isCrawlDataError=false;


        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(thumbnailProcData.getSavePathPrefix(), thumbnailProcData.getCpName());

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader =
                new HttpRequestHeader(thumbnailProcData.getHostDomain(), thumbnailProcData.getHostReferer());
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(thumbnailProcData.getHtmlCrawlConnectionTimeout());
        crawlSite.setSocketTimeout(thumbnailProcData.getHtmlCrawlReadTimeout());
        crawlSite.setCrawlEncode(thumbnailProcData.getHtmlCrawlEncoding());

        ////////////////////////////////////////////////////////////////////////
        // search 테이블에서 cp에 해당되는 모든 데이터를 read.
        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(thumbnailProcData.getCpName());
        for (Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

            crawlSite.setCrawlUrl(searchData.getContentUrl());

            ////////////////////////////////////////////////////////////////////////
            // thumbnail 추출할 상품정보 html crawl.
            ////////////////////////////////////////////////////////////////////////
            crawlErrorCount = 0;
            isCrawlDataError = false;
            for (;;) {
                try {
                    returnCode = crawlSite.HttpCrawlGetDataTimeout();
                    if (returnCode != 200 && returnCode != 201) {
                        logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                        isCrawlDataError = true;
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (crawlErrorCount >= 3) {
                        isCrawlDataError = true;
                        break;
                    }
                    crawlErrorCount++;
                    logger.error(" 추출할 상품정보 html 수집중 Exception 발생 " + Arrays.toString(e.getStackTrace()));
                    logger.error(" 수집 에러 발생 URL : " + crawlSite.getCrawlUrl());
                }
            }

            if (isCrawlDataError) {
                continue;
            }

            ////////////////////////////////////////////////////////////////////////
            // thumbnail url 추출.
            ////////////////////////////////////////////////////////////////////////
            if (thumbnailProcData.getParserType()==1) {
                doc = Jsoup.parse(crawlSite.getCrawlData());
                elements = doc.select(thumbnailProcData.getParserGroupSelect());
                for (Element element : elements) {
                    if (thumbnailProcData.getParserSkipPattern().length() > 0) {
                        if (!element.outerHtml().contains(thumbnailProcData.getParserSkipPattern()))
                            continue;
                    }
                    document = Jsoup.parse(element.outerHtml());
                    listE = document.select(thumbnailProcData.getParserDocumentSelect());
                    for (Element et : listE) {
                        strItem = et.attr("src");
                        if (thumbnailProcData.getReplacePatternFindData().length()>0 && strItem.contains(thumbnailProcData.getReplacePatternFindData())) {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() +
                                    strItem.replace(thumbnailProcData.getReplacePatternSource(), thumbnailProcData.getReplacePatternDest()));
                            break;
                        } else {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() + strItem);
                            break;
                        }
                    }
                    break;
                }
            } else {
                logger.error(" Parser Type is NULL !!");
                continue;
            }

            ////////////////////////////////////////////////////////////////////////
            // 추출한 thumbnail을 thumb 테이블에 저장.
            ////////////////////////////////////////////////////////////////////////
            imageSaveErrorCount = 0;
            for (;;) {
                try {
                    // 기존 thumbnail이 있는지 찾는다.
                    thumbnailData.setCpName(thumbnailProcData.getCpName());
                    thumbnailData.setProductId(searchData.getProductId());
                    thumbnailData.setBigThumbUrl(searchData.getThumbUrlBig());
                    dbThumbnailData = thumbnailDataService.getFindThumbnailData(thumbnailData);

                    // thumb 테이블에 thumbnail url이 없을 경우.
                    if (dbThumbnailData==null || dbThumbnailData.getBigThumbUrl().trim().length()==0) {
                        // thumb_url_big URL에서 파일이름을 추출.
                        imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                        // 본문에서 big 이미지를 download 한다.
                        globalUtils.saveDiskImgage(
                                thumbnailProcData.getSavePathPrefix(),
                                thumbnailProcData.getCpName(), searchData.getThumbUrlBig(), imageFileName);
                        // thumb 테이블에 thumbnail 데이터 insert.
                        thumbnailDataService.insertThumbnailData(thumbnailData);
                        logger.info(String.format(" Insert thumbnail cp(%s),pid(%s)",
                                thumbnailProcData.getCpName(), searchData.getProductId()));
                    } else {
                        // 기존 thumbnail 데이터가 있어도 모두 업데이트 한다.
                        if (thumbnailProcData.isAllDataCrawl()) {
                            // thumb_url_big URL에서 파일이름을 추출.
                            imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                            // 본문에서 big 이미지를 download 한다.
                            globalUtils.saveDiskImgage(
                                    thumbnailProcData.getSavePathPrefix(),
                                    thumbnailProcData.getCpName(), searchData.getThumbUrlBig(), imageFileName);
                            // thumb 테이블에서 기존 데이터를 update 한다.
                            thumbnailDataService.updateThumbnailData(thumbnailData);
                            logger.info(String.format(" Update thumbnail cp(%s),pid(%s)",
                                    thumbnailProcData.getCpName(), searchData.getProductId()));
                        } else {
                            logger.info(String.format(" Exist skip update thumbnail cp(%s),pid(%s)",
                                    thumbnailProcData.getCpName(), searchData.getProductId()));
                        }
                    }
                    break;
                } catch (Exception e) {
                    if (imageSaveErrorCount > 3) {
                        logger.error(" Failure Download image breaking over MAX COUNT retry!!");
                        break;
                    }
                    logger.error(String.format(" Download image (%s) failure (%s), Count(%d)",
                            searchData.getThumbUrlBig(), e.getStackTrace().toString(),imageSaveErrorCount));
                    imageSaveErrorCount++;
                    Thread.sleep(1100);
                }
            }
        }
    }


    /////////////////////////////////////////////////////////////////
    // target url에서 이미지 다운로드.
    public void thumbnailProcessingTargetURL(ThumbnailProcData thumbnailProcData, String bodyUrl) throws Exception {
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        int returnCode, crawlErrorCount, imageSaveErrorCount;
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnailData;
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        SearchData searchData;
        String strItem;
        String key, imageFileName;
        boolean isCrawlDataError=false;


        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(thumbnailProcData.getSavePathPrefix(), thumbnailProcData.getCpName());

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader =
                new HttpRequestHeader(thumbnailProcData.getHostDomain(), thumbnailProcData.getHostReferer());
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(thumbnailProcData.getHtmlCrawlConnectionTimeout());
        crawlSite.setSocketTimeout(thumbnailProcData.getHtmlCrawlReadTimeout());
        crawlSite.setCrawlEncode(thumbnailProcData.getHtmlCrawlEncoding());

        ////////////////////////////////////////////////////////////////////////
        // search 테이블에서 cp에 해당되는 모든 데이터를 read.
        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(thumbnailProcData.getCpName());
        for (Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

            if (!bodyUrl.equals(searchData.getContentUrl())) continue;

            crawlSite.setCrawlUrl(searchData.getContentUrl());

            ////////////////////////////////////////////////////////////////////////
            // thumbnail 추출할 상품정보 html crawl.
            ////////////////////////////////////////////////////////////////////////
            crawlErrorCount = 0;
            isCrawlDataError = false;
            for (;;) {
                try {
                    returnCode = crawlSite.HttpCrawlGetDataTimeout();
                    if (returnCode != 200 && returnCode != 201) {
                        logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                        isCrawlDataError = true;
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (crawlErrorCount >= 3) {
                        isCrawlDataError = true;
                        break;
                    }
                    crawlErrorCount++;
                    logger.error(" 추출할 상품정보 html 수집중 Exception 발생 " + Arrays.toString(e.getStackTrace()));
                    logger.error(" 수집 에러 발생 URL : " + crawlSite.getCrawlUrl());
                }
            }

            if (isCrawlDataError) {
                continue;
            }

            ////////////////////////////////////////////////////////////////////////
            // thumbnail url 추출.
            ////////////////////////////////////////////////////////////////////////
            if (thumbnailProcData.getParserType()==1) {
                doc = Jsoup.parse(crawlSite.getCrawlData());
                elements = doc.select(thumbnailProcData.getParserGroupSelect());
                for (Element element : elements) {
                    if (thumbnailProcData.getParserSkipPattern().length() > 0) {
                        if (!element.outerHtml().contains(thumbnailProcData.getParserSkipPattern()))
                            continue;
                    }
                    document = Jsoup.parse(element.outerHtml());
                    listE = document.select(thumbnailProcData.getParserDocumentSelect());
                    for (Element et : listE) {
                        strItem = et.attr("src");
                        if (thumbnailProcData.getReplacePatternFindData().length()>0 && strItem.contains(thumbnailProcData.getReplacePatternFindData())) {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() +
                                    strItem.replace(thumbnailProcData.getReplacePatternSource(), thumbnailProcData.getReplacePatternDest()));
                            break;
                        } else {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() + strItem);
                            break;
                        }
                    }
                    break;
                }
            } else {
                logger.error(" Parser Type is NULL !!");
                continue;
            }

            ////////////////////////////////////////////////////////////////////////
            // 추출한 thumbnail을 thumb 테이블에 저장.
            ////////////////////////////////////////////////////////////////////////
            imageSaveErrorCount = 0;
            for (;;) {
                try {
                    // 기존 thumbnail이 있는지 찾는다.
                    thumbnailData.setCpName(thumbnailProcData.getCpName());
                    thumbnailData.setProductId(searchData.getProductId());
                    // thumb 테이블에 있는 데이터를 가져온다.
                    dbThumbnailData = thumbnailDataService.getFindThumbnailData(thumbnailData);

                    // thumb 테이블에 thumbnail url이 없을 경우.
                    if (dbThumbnailData==null || dbThumbnailData.getBigThumbUrl().trim().length()==0) {
                        // thumb_url_big URL에서 파일이름을 추출.
                        imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());

                        // 본문에서 big 이미지를 download 한다.
                        globalUtils.saveDiskImgage(thumbnailProcData.getSavePathPrefix(),
                                thumbnailProcData.getCpName(), searchData.getThumbUrlBig(), imageFileName);

                        // 추출한 image url을 저장하고.
                        thumbnailData.setBigThumbUrl(searchData.getThumbUrlBig());

                        // thumb 테이블에 thumbnail 데이터 insert.
                        thumbnailDataService.insertThumbnailData(thumbnailData);

                        logger.info(String.format(" Insert thumbnail cp(%s),pid(%s)",
                                thumbnailProcData.getCpName(), searchData.getProductId()));
                    } else {
                        // 기존 thumbnail 데이터가 있어도 모두 업데이트 한다.
                        if (thumbnailProcData.isAllDataCrawl()) {
                            // thumb_url_big URL에서 파일이름을 추출.
                            imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());

                            // 본문에서 big 이미지를 download 한다.
                            globalUtils.saveDiskImgage(
                                    thumbnailProcData.getSavePathPrefix(),
                                    thumbnailProcData.getCpName(), searchData.getThumbUrlBig(), imageFileName);

                            // 추출한 image url을 저장하고.
                            thumbnailData.setBigThumbUrl(searchData.getThumbUrlBig());

                            // thumb 테이블에서 기존 데이터를 update 한다.
                            thumbnailDataService.updateThumbnailData(thumbnailData);

                            logger.info(String.format(" Update thumbnail cp(%s),pid(%s)",
                                    thumbnailProcData.getCpName(), searchData.getProductId()));
                        } else {
                            logger.info(String.format(" Exist skip update thumbnail cp(%s),pid(%s)",
                                    thumbnailProcData.getCpName(), searchData.getProductId()));
                        }
                    }
                    break;
                } catch (Exception e) {
                    if (imageSaveErrorCount > 3) {
                        logger.error(" Failure Download image breaking over MAX COUNT retry!!");
                        break;
                    }
                    logger.error(String.format(" Download image (%s) failure (%s), Count(%d)",
                            searchData.getThumbUrlBig(), e.getStackTrace().toString(),imageSaveErrorCount));
                    imageSaveErrorCount++;
                    Thread.sleep(1100);
                }
            }
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // target url에서 이미지 다운로드.
    public void thumbnailProcessingSingle(ThumbnailProcData thumbnailProcData, String bodyUrl) throws Exception {
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        int returnCode, crawlErrorCount, imageSaveErrorCount;
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnailData;
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        String strItem;
        String imageFileName;
        boolean isCrawlDataError;


        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(thumbnailProcData.getSavePathPrefix(), thumbnailProcData.getCpName());

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader =
                new HttpRequestHeader(thumbnailProcData.getHostDomain(), thumbnailProcData.getHostReferer());
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(thumbnailProcData.getHtmlCrawlConnectionTimeout());
        crawlSite.setSocketTimeout(thumbnailProcData.getHtmlCrawlReadTimeout());
        crawlSite.setCrawlEncode(thumbnailProcData.getHtmlCrawlEncoding());

        crawlSite.setCrawlUrl(bodyUrl);

        ////////////////////////////////////////////////////////////////////////
        // thumbnail 추출할 상품정보 html crawl.
        ////////////////////////////////////////////////////////////////////////
        crawlErrorCount = 0;
        isCrawlDataError = false;
        for (;;) {
            try {
                returnCode = crawlSite.HttpCrawlGetDataTimeout();
                if (returnCode != 200 && returnCode != 201) {
                    logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                    isCrawlDataError = true;
                } else {
                    break;
                }
            } catch (Exception e) {
                if (crawlErrorCount >= 3) {
                    isCrawlDataError = true;
                    break;
                }
                crawlErrorCount++;
                logger.error(" 추출할 상품정보 html 수집중 Exception 발생 " + Arrays.toString(e.getStackTrace()));
                logger.error(" 수집 에러 발생 URL : " + crawlSite.getCrawlUrl());
            }
        }

        if (isCrawlDataError) {
            logger.error(String.format(" isCrawlDataError count MAX & return"));
            return;
        }

        ////////////////////////////////////////////////////////////////////////
        // thumbnail url 추출.
        ////////////////////////////////////////////////////////////////////////
        if (thumbnailProcData.getParserType()==1) {
            doc = Jsoup.parse(crawlSite.getCrawlData());
            elements = doc.select(thumbnailProcData.getParserGroupSelect());
            for (Element element : elements) {
                if (thumbnailProcData.getParserSkipPattern().length() > 0) {
                    if (!element.outerHtml().contains(thumbnailProcData.getParserSkipPattern()))
                        continue;
                }
                document = Jsoup.parse(element.outerHtml());
                listE = document.select(thumbnailProcData.getParserDocumentSelect());
                for (Element et : listE) {
                    strItem = et.attr("src");
                    if (thumbnailProcData.getReplacePatternFindData().length()>0 && strItem.contains(thumbnailProcData.getReplacePatternFindData())) {
                        logger.info(thumbnailProcData.getPrefixHostThumbUrl() +
                                strItem.replace(thumbnailProcData.getReplacePatternSource(), thumbnailProcData.getReplacePatternDest()));
                        break;
                    } else {
                        logger.info(thumbnailProcData.getPrefixHostThumbUrl() + strItem);
                        break;
                    }
                }
                break;
            }
        } else {
            logger.error(" Parser Type is NULL !!");
            return;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws Exception {
        ThumbnailProc thumbnailProc = new ThumbnailProc();
        ThumbnailProcData thumbnailProcData = new ThumbnailProcData();


        if (args.length==0) {
            logger.error(" USAGE: cp_name crawl_thumb_in_body_url");
            System.exit(-1);
        }

        String cpName = args[0];
        String bodyUrl = args[1];
        boolean isAllData=true;

        thumbnailProcData.setCpName(cpName);
        thumbnailProcData.setIsAllDataCrawl(isAllData);
        thumbnailProcData.setHtmlCrawlConnectionTimeout(5000);
        thumbnailProcData.setHtmlCrawlReadTimeout(10000);
        thumbnailProcData.setSavePathPrefix("/Users/baeonejune/work/SummaryNode/images");
        thumbnailProcData.setParserType(1);
        thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
        thumbnailProcData.setPrefixHostThumbUrl("http://goodcamping.net");
        thumbnailProcData.setHostReferer("http://goodcamping.net");
        thumbnailProcData.setHostDomain("goodcamping.net");

        thumbnailProcData.setParserGroupSelect("span[style=\"cursor:pointer\"]");
        thumbnailProcData.setParserSkipPattern("objImg");
        thumbnailProcData.setParserDocumentSelect("img");
        thumbnailProcData.setReplacePatternFindData("../data");
        thumbnailProcData.setReplacePatternSource("../data");
        thumbnailProcData.setReplacePatternDest("/shop/data");

        thumbnailProc.thumbnailProcessingSingle(thumbnailProcData, bodyUrl);
    }
}
