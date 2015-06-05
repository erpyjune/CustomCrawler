package com.erpy.thumbnail;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
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
public class Thumbnail {

    private static Logger logger = Logger.getLogger("Thumbnail");
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
        String prefixHostThumbUrl="http://www.airmt.net";
        String hostDomain = "www.airmt.net";

        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(localPath, cpName);

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(hostDomain, prefixHostThumbUrl);
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(10000);
        crawlSite.setCrawlEncode("euc-kr");

        ////////////////////////////////////////////////////////////////////////
        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(cpName);
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
            elements = doc.select("div[style=\"padding-bottom:10\"]");
            for (Element element : elements) {
                document = Jsoup.parse(element.outerHtml());
                listE = document.select("span img");
                for (Element et : listE) {
                    strItem = et.attr("src");
                    if (strItem.contains("../data/goods")) {
                        // 본문 정보에서 추출한 큰 이미지를 thumb_url_big에 임시 저장.
                        searchData.setThumbUrlBig(prefixHostThumbUrl + strItem.replace("../data", "/shop/data"));
//                        logger.info(prefixHostThumbUrl + strItem.replace("../data", "/shop/data"));
                        break;
                    } else {
                        searchData.setThumbUrlBig(prefixHostThumbUrl + strItem);
//                        logger.info(prefixHostThumbUrl + strItem);
                    }
                }
            }

            imageSaveErrorCount=0;

            while(true) {
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
                }
            }
        }
    }
}
