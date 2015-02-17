package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.*;
import com.erpy.extract.ExtractInfo;
import com.erpy.extract.ExtractProperties;
import com.erpy.io.FileIO;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class OkMallProc {

    private static Logger logger = Logger.getLogger(OkMallProc.class.getName());

    private String filePath;
    private String keyword;
    private String txtEncode="euc-kr";
    private static CrawlDataService crawlDataService;

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

    public Map<String, SearchData> extractOkMall() throws Exception {
        FileIO fileIO = new FileIO();
        ExtractInfo extractInfo = new ExtractInfo();
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Elements elements=null;
        Elements elementsLink=null;
        Document document=null;
        String strItem=null;

        fileIO.setEncoding(this.txtEncode);
        fileIO.setPath(this.filePath);


        ////////////////////////////////////////////////////////
        if (filePath==null) {
            logger.warning("(ERROR) filePath is null !!");
            System.exit(-1);
        }

        String htmlContent = fileIO.getFileContent();
        Document doc = Jsoup.parse(htmlContent);

        Elements listE;
        Document docu;
        String strLinkUrl=null;

        elements = doc.select(extractInfo.getOkmallProf().getListGroup());
        for (Element element : elements) {

            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Link
            listE = document.select(extractInfo.getOkmallProf().getLinkGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getLink());
                for (Element elink : elementsLink) {
                    strItem = elink.attr(extractInfo.getOkmallProf().getLinkAttr());
                    strLinkUrl = strItem; // Used map key.
                    searchData.setContentUrl(strItem);
//                    System.out.println(String.format(">> Link : %s", strItem));
                }
            }

            // Thumb link
            listE = document.select(extractInfo.getOkmallProf().getThumbGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getThumb());
                for (Element elink : elementsLink) {
                    strItem = elink.attr(extractInfo.getOkmallProf().getThumbAttr());
                    searchData.setThumbUrl(strItem);
//                    System.out.println(String.format(">> Thumb : %s", strItem));
                }
            }

            // brand name
            //listE = document.select("div.val_top");
            listE = document.select(extractInfo.getOkmallProf().getBrandNameGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                // brand name
                //elementsLink = docu.select("div.brand_detail_layer p.item_title a span.prName_Brand");
                elementsLink = docu.select(extractInfo.getOkmallProf().getBrandName());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("[","").replace("]", "").replace("\"", " ").replace("'", " ");
                    searchData.setBrandName(strItem);
//                    System.out.println(strItem);
                }
            }

            // product name
            //listE = document.select("div.val_top");
            listE = document.select(extractInfo.getOkmallProf().getProductNameGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                // product name
                //elementsLink = docu.select("div.brand_detail_layer p.item_title a span.prName_PrName");
                elementsLink = docu.select(extractInfo.getOkmallProf().getProductName());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("\"", " ").replace("'", " ");
                    searchData.setProductName(strItem);
                    searchData.setCpName("okmall");
                    searchData.setCrawlKeyword(this.keyword);
//                    System.out.println(strItem);
                }
            }

            // sale per
            listE = document.select(extractInfo.getOkmallProf().getSalePerGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getSalePer());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("%", "").replace(" ", "");
                    searchData.setSalePer(Float.parseFloat(strItem));
//                    System.out.println(String.format(">> Sale Per : %s", strItem));
                    break;
                }
            }

            // org price
            //listE = document.select("div.al_left");
            listE = document.select(extractInfo.getOkmallProf().getOrgPriceGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                //elementsLink = docu.select("div.real_price div.value_price span.l span");
                elementsLink = docu.select(extractInfo.getOkmallProf().getOrgOrgPrice());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("원","").replace(",", "");
                    searchData.setOrgPrice(Integer.parseInt(strItem));
//                    System.out.println(String.format(">> Org Per : %s", strItem));
                    break;
                }
            }

            // sale price
            //listE = document.select("div.al_left");
            listE = document.select(extractInfo.getOkmallProf().getSalePriceGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                //elementsLink = docu.select("div.real_price div.last_price span.l span");
                elementsLink = docu.select(extractInfo.getOkmallProf().getSalePrice());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("원","").replace(",","").replace(" ", "");
//                    System.out.println(String.format(">> Sale Per : %s", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                }
            }

            searchDataMap.put(strLinkUrl, searchData);
        }

        logger.info("this file extract size is " + searchDataMap.size());

        return searchDataMap;
    }


    public void insertOkMall(Map<String, SearchData> sdAll) throws IOException {
        SearchDataService searchDataService = new SearchDataService();
        SearchData sd = new SearchData();
        String strUrlLink=null;

        for(Map.Entry<String, SearchData> entry : sdAll.entrySet()) {
            //strUrlLink = entry.getKey();
            sd = entry.getValue();

            if (sd.getType().isEmpty()) {

                logger.warning(String.format("UPDATEED(empty) : (%d)(%s)(%s)",
                        sd.getDataId(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
            }
            else if (sd.getType().equals("insert")) {

                logger.warning(String.format("INSERTED : %f|%d|%d|%s|%s",
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
            }
            else if (sd.getType().equals("update")) {

                logger.warning(String.format("UPDATED : %d|%f|%d|%d|%s|%s",
                        sd.getDataId(),
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.updateSearchData(sd);
            }
            else {
                logger.warning(String.format("data biz is not (inset or update) %d", sd.getDataId()));
            }

        }
    }


    public int checkDataCount(String path, String readEncoding) throws IOException {
        String patten = "div.item_box div.val_top div.brand_detail_layer p.item_title a span.prName_PrName";
        FileIO fileIO = new FileIO();
        fileIO.setPath(path);
        fileIO.setEncoding(readEncoding);

        String data = fileIO.getFileContent();
        Document doc = Jsoup.parse(data);
        Elements elements = doc.select(patten);
        return elements.size();
    }


    public Map<String, SearchData> checkSearchDataValid(
            Map<String, SearchData> all, Map<String, SearchData> part) throws Exception {

        String strUrlLink;
        SearchData searchDataPart = new SearchData();
        SearchData searchDataAll = new SearchData();
        Map<String, SearchData> newSearchDataMap = new HashMap<String, SearchData>();

        for(Map.Entry<String, SearchData> entry : part.entrySet()) {
            strUrlLink = entry.getKey();
            searchDataPart = entry.getValue();

            if (isNull(searchDataPart)) continue;

            if (all.containsKey(strUrlLink)) {

                searchDataAll = all.get(strUrlLink);

                if (isNull(searchDataAll)) continue;

                // 동일한 데이터일 경우.
                if (searchDataAll.getSalePrice().equals(searchDataPart.getSalePrice())
                        && searchDataAll.getProductName().equals(searchDataPart.getProductName())
                        && searchDataAll.getOrgPrice().equals(searchDataPart.getOrgPrice())) {

//                    logger.info(String.format("same all(%d)(%d)(%s)",
//                            searchDataAll.getSalePrice(),
//                            searchDataAll.getOrgPrice(),
//                            searchDataAll.getProductName()));
//                    logger.info(String.format("same part(%d)(%d)(%s)",
//                            searchDataPart.getSalePrice(),
//                            searchDataPart.getOrgPrice(),
//                            searchDataPart.getProductName()));
//                    logger.info("same thing ================================");
                }
                // url은 동일하지만 데이터가 다른 경우.
                else {
//                    logger.info("UPDATE ================================");
//                    logger.info(String.format("all (%d)(%f)(%d)(%d)(%s)(%s)",
//                            searchDataAll.getDataId(),
//                            searchDataAll.getSalePer(),
//                            searchDataAll.getSalePrice(),
//                            searchDataAll.getOrgPrice(),
//                            searchDataAll.getProductName(),
//                            searchDataAll.getContentUrl()));
//                    logger.info(String.format("part (%f)(%d)(%d)(%s)(%s)",
//                            searchDataPart.getSalePer(),
//                            searchDataPart.getSalePrice(),
//                            searchDataPart.getOrgPrice(),
//                            searchDataPart.getProductName(),
//                            searchDataPart.getContentUrl()));
//                    logger.info("UPDATE ================================");

                    searchDataPart.setType("update");
                    searchDataPart.setDataStatus("U");
                    searchDataPart.setDataId(searchDataAll.getDataId());
                    newSearchDataMap.put(strUrlLink, searchDataPart);
                }
            }
            // 동일한 url이 없는 경우.
            else {
                searchDataPart.setType("insert");
                searchDataPart.setDataStatus("I");
                newSearchDataMap.put(strUrlLink, searchDataPart);
            }
        }

        return newSearchDataMap;
    }


    public String makeUrlPage(String url, int page) throws IOException {
        return new String(String.format("%s&page=%d",url, page));
    }

    public void crawlData(String url, String strKeyword, String strCpName) throws IOException {
        Random random = new Random();
        DateInfo dateInfo = new DateInfo();
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        CrawlData crawlData = new CrawlData();
        GlobalInfo globalInfo = new GlobalInfo();
        crawlDataService = new CrawlDataService();

        int page=0;
        long randomNum=0;
        int data_size=0;
        String strUrl;
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(5000);
        crawlSite.setCrawlEncode(txtEncode);

        for(;;) {

            strUrl = String.format("%s&page=%d", url, page);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);
            crawlSite.HttpCrawlGetDataTimeout();

            // save crawling file.
            randomNum = random.nextInt(918277377);
            crawlSavePath = savePrefixPath + "/" + strCpName + "/" + Long.toString(randomNum) + ".html";
            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            crawlIO.executeSaveData();

            data_size = checkDataCount(crawlSavePath, txtEncode);
            if (data_size <= 0) {
                logger.info("=================================");
                logger.info("last page : " + strUrl);
                break;
            }

            // insert save file to history db.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            crawlDataService.insertCrawlData(crawlData);
            logger.info(String.format("crawl(%d) %s", data_size, strUrl));
            page++;
        }
    }


    public void indexingOkMall(SearchData searchData) throws IOException {
        int returnCode;
        StringBuffer sb = new StringBuffer();
        StringBuffer indexUrl = new StringBuffer("http://localhost:9200/shop/okmall/");
        CrawlSite crawlSite = new CrawlSite();

        sb.append("{");

        sb.append("\"dataid\" : ");
        sb.append("\"").append(searchData.getDataId()).append("\",");

        sb.append("\"product_name\" : ");
        sb.append("\"").append(searchData.getProductName()).append("\",");

        sb.append("\"brand_name\" : ");
        sb.append("\"").append(searchData.getBrandName()).append("\",");

        sb.append("\"url\" : ");
        sb.append("\"http://www.okmall.com/").append(searchData.getContentUrl()).append("\",");

        sb.append("\"thumb\" : ");
        sb.append("\"").append(searchData.getThumbUrl()).append("\",");

        sb.append("\"org_price\" : ");
        sb.append(" ").append(searchData.getOrgPrice()).append(",");

        sb.append("\"sale_price\" : ");
        sb.append(" ").append(searchData.getSalePrice()).append(",");

        sb.append("\"sale_per\" : ");
        sb.append(" ").append(searchData.getSalePer()).append(",");

        sb.append("\"cp\" : ");
        sb.append("\"").append(searchData.getCpName()).append("\",");

        sb.append("\"keyword\" : ");
        sb.append("\"").append(searchData.getCrawlKeyword()).append("\"");

        sb.append("}");

        indexUrl.append(searchData.getDataId());
        crawlSite.setCrawlUrl(indexUrl.toString());
        crawlSite.setCrawlData(sb.toString());
        returnCode = crawlSite.HttpXPUT();

        logger.info(String.format("[%d]:%s",returnCode, indexUrl));
    }

    private boolean isNull(SearchData sd) {
        if (sd.getSalePrice()==null) return true;
        if (sd.getOrgPrice()==null) return true;
        if (sd.getProductName()==null) return true;

        return false;
    }
}
