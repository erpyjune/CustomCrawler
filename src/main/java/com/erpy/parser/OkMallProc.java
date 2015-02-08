package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
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

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class OkMallProc {
    private String filePath;
    private String keyword;
    private String txtEncode="euc-kr";
    private Integer size=0;
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

    public List<SearchData> extractOkMall() throws Exception {
        FileIO fileIO = new FileIO();
        ExtractInfo extractInfo = new ExtractInfo();
        List<SearchData> searchDataList = new ArrayList<SearchData>();
        Elements elements=null;
        Elements elementsLink=null;
        Document document=null;
        String strItem=null;

        fileIO.setEncoding(this.txtEncode);
        fileIO.setPath(this.filePath);


        ////////////////////////////////////////////////////////
        if (filePath==null) {
            System.out.println("(ERROR) filePath is null !!");
            System.exit(0);
        }

        String htmlContent = fileIO.getFileContent();
        Document doc = Jsoup.parse(htmlContent);

        Elements listE;
        Document docu;
        size = 0;
        //elements = doc.select("div.item_box[data-ProductNo]");
        elements = doc.select(extractInfo.getOkmallProf().getListGroup());
        for (Element element : elements) {

            SearchData searchData = new SearchData();
            document = Jsoup.parse(element.outerHtml());

            // Link
            //listE = document.select("div.os_border");
            listE = document.select(extractInfo.getOkmallProf().getLinkGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                //elementsLink = docu.select("a");
                elementsLink = docu.select(extractInfo.getOkmallProf().getLink());
                for (Element elink : elementsLink) {
                    //strItem = elink.attr("href");
                    strItem = elink.attr(extractInfo.getOkmallProf().getLinkAttr());
                    searchData.setContentUrl(strItem);
                    System.out.println(String.format(">> Link : %s", strItem));
                }
            }

            // Thumb link
            //listE = document.select("div.os_border");
            listE = document.select(extractInfo.getOkmallProf().getThumbGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                //elementsLink = docu.select("a img");
                elementsLink = docu.select(extractInfo.getOkmallProf().getThumb());
                for (Element elink : elementsLink) {
                    //strItem = elink.attr("data-original");
                    strItem = elink.attr(extractInfo.getOkmallProf().getThumbAttr());
                    searchData.setThumbUrl(strItem);
                    System.out.println(String.format(">> Thumb : %s", strItem));
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
            //listE = document.select("div.al_left");
            listE = document.select(extractInfo.getOkmallProf().getSalePerGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                //elementsLink = docu.select("div.icon_group.clearfix div.ic.ic_coupon div.icon");
                elementsLink = docu.select(extractInfo.getOkmallProf().getSalePer());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("%", "").replace(" ", "");
                    searchData.setSalePer(Float.parseFloat(strItem));
                    System.out.println(String.format(">> Sale Per : %s", strItem));
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
                    System.out.println(String.format(">> Org Per : %s", strItem));
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
                    System.out.println(String.format(">> Sale Per : %s", strItem));
                    searchData.setSalePrice(Integer.parseInt(strItem));
                    break;
                }
            }

            // add ListArray.
            searchDataList.add(searchData);
            size++;
        }

        System.out.println("okmall extract size : " + size);

        return searchDataList;
    }


    public void insertOkMall(List<SearchData> searchDataList) throws IOException {
        SearchDataService searchDataService = new SearchDataService();
        SearchData searchData = new SearchData();
        Iterator iterator = searchDataList.iterator();
        while(iterator.hasNext()) {
            searchData = (SearchData)iterator.next();
            searchDataService.insertSearchData(searchData);
//            System.out.println(searchData.getProductName());
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
        int randomNum=0;
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
            randomNum = random.nextInt(9182773);
            crawlSavePath = savePrefixPath + "/" + strCpName + "/" + Integer.toString(randomNum) + ".html";
            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            crawlIO.executeSaveData();

            data_size = checkDataCount(crawlSavePath, txtEncode);
            if (data_size <= 0) {
                System.out.println("Empty data : " + strUrl);
                break;
            }

            // insert save file to history db.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            crawlDataService.insertCrawlData(crawlData);
            System.out.println(String.format("crawl(%d) %s", data_size, strUrl));
            data_size = 0;
            page++;
        }
    }


    public void indexingOkMall(SearchData searchData) throws IOException {
        int returnCode;
        StringBuilder sb = new StringBuilder();
        StringBuilder indexUrl = new StringBuilder("http://localhost:9200/shop/okmall/");
        CrawlSite crawlSite = new CrawlSite();

        sb.append("{");

        sb.append("\"dataid\" : ");
        sb.append("\""+searchData.getDataId()+"\",");

        sb.append("\"product_name\" : ");
        sb.append("\""+searchData.getProductName()+"\",");

        sb.append("\"brand_name\" : ");
        sb.append("\""+searchData.getBrandName()+"\",");

        sb.append("\"url\" : ");
        sb.append("\"http://www.okmall.com/"+searchData.getContentUrl()+"\",");

        sb.append("\"thumb\" : ");
        sb.append("\""+searchData.getThumbUrl()+"\",");

        sb.append("\"org_price\" : ");
        sb.append(" "+searchData.getOrgPrice()+",");

        sb.append("\"sale_price\" : ");
        sb.append(" "+searchData.getSalePrice()+",");

        sb.append("\"sale_per\" : ");
        sb.append(" "+searchData.getSalePer()+",");

        sb.append("\"cp\" : ");
        sb.append("\""+searchData.getCpName()+"\",");

        sb.append("\"keyword\" : ");
        sb.append("\""+searchData.getCrawlKeyword()+"\"");

        sb.append("}");

        indexUrl.append(searchData.getDataId());
        crawlSite.setCrawlUrl(indexUrl.toString());
        crawlSite.setCrawlData(sb.toString());
        System.out.println("indexing url : " + indexUrl.toString());
        System.out.println("product name : " + searchData.getProductName());

        returnCode = crawlSite.HttpXPUT();
        System.out.println("return code : " + returnCode);
        System.out.println("---------------------------------------------");
    }
}
