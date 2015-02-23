package com.erpy.parser;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.dao.*;
import com.erpy.extract.ExtractInfo;
import com.erpy.io.FileIO;
import com.erpy.utils.DateInfo;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Created by baeonejune on 14. 12. 27..
 */
public class OkMallProc {

    private static Logger logger = Logger.getLogger(OkMallProc.class.getName());
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

    public Map<String, SearchData> extractOkMall() throws Exception {
        FileIO fileIO = new FileIO();
        ExtractInfo extractInfo = new ExtractInfo();
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        Elements elements;
        Elements elementsLink;
        Document document;
        String strItem;
        String productId;
        boolean isMan=false, isWoman=false;
        Elements listE;
        Document docu;
        String strLinkUrl=null;


        fileIO.setEncoding(txtEncode);
        fileIO.setPath(filePath);

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
        elements = doc.select(extractInfo.getOkmallProf().getListGroup());
        for (Element element : elements) {

            productId=null;
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
                // extract productID
                productId = getFieldData(strLinkUrl,"no=", "&").trim();
                searchData.setProductId(productId);
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

            // Sex
            listE = document.select(extractInfo.getOkmallProf().getSexGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getSex());
                isMan = false;
                isWoman = false;
                for (Element elink : elementsLink) {
                    strItem = elink.attr(extractInfo.getOkmallProf().getSexAttr());
                    if (strItem.contains("남성용")) isMan=true;
                    if (strItem.contains("여성용")) isWoman=true;
//                    searchData.setThumbUrl(strItem);
//                    System.out.println(String.format(">> Sex : %s", strItem));
                }

                // 남성용, 여성용, 남녀공통
                if (isMan && !isWoman) searchData.setbMan(true);
                if (!isMan && isWoman) searchData.setbWoman(true);
                if (isMan && isWoman) {
                    searchData.setbMan(true);
                    searchData.setbWoman(true);
                }

//                if (!isMan && !isWoman) logger.info("체크불량");
//                if (isMan && !isWoman) logger.info("남성용");
//                if (!isMan && isWoman) logger.info("여성용");
//                if (isMan && isWoman) logger.info("남성 여성 공용");
            }

            // brand name
            //listE = document.select("div.val_top");
            listE = document.select(extractInfo.getOkmallProf().getBrandNameGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getBrandName());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("[","").replace("]", "").replace("\"", " ").replace("'", " ");
                    searchData.setBrandName(strItem);
                }
            }

            // product name
            listE = document.select(extractInfo.getOkmallProf().getProductNameGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getProductName());
                for (Element elink : elementsLink) {
                    strItem = elink.text();
                    strItem = strItem.replace("\"", " ").replace("'", " ");
                    searchData.setProductName(strItem);
                    searchData.setCpName("okmall");
                    searchData.setCrawlKeyword(isSexKeywordAdd(keyword, isMan, isWoman));
                }
            }

            // sale per
            listE = document.select(extractInfo.getOkmallProf().getSalePerGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getSalePer());
                for (Element elink : elementsLink) {
                    strItem = elink.text().trim();
                    strItem = strItem.replace("%", "").replace(" ", "");
                    if (isAllFloatChar(strItem)) {
                        searchData.setSalePer(Float.parseFloat(strItem));
                        break;
                    } else {
                        logger.error(String.format(" Extract [sale per] data is NOT valid - %s", strItem));
                    }
                }
            }

            // org price
            listE = document.select(extractInfo.getOkmallProf().getOrgPriceGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getOrgOrgPrice());
                for (Element elink : elementsLink) {
                    strItem = elink.text().trim();
                    strItem = strItem.replace("원","").replace(",", "");
                    if (isAllDigitChar(strItem)) {
                        searchData.setOrgPrice(Integer.parseInt(strItem));
                        break;
                    } else {
                        // org price가 없는것은 에러 이다.
                        // 아래 map에 데이터 넣기전 체크할때 걸려서 skip 하게 된다.
                        logger.error(String.format(" Extract [org price] data is NOT valid - %s", strItem));
                    }
                }
            }

            // sale price
            listE = document.select(extractInfo.getOkmallProf().getSalePriceGroup());
            for (Element et : listE) {
                docu = Jsoup.parse(et.outerHtml());
                elementsLink = docu.select(extractInfo.getOkmallProf().getSalePrice());
                for (Element elink : elementsLink) {
                    strItem = elink.text().trim();
                    strItem = strItem.replace("원","").replace(",","").replace(" ", "");
                    if (isAllDigitChar(strItem)) {
                        searchData.setSalePrice(Integer.parseInt(strItem));
                        break;
                    } else {
                        // sale price는 없을경우 77777777 입력한다. 하지만 에러는 프린트 한다.
                        searchData.setSalePrice(77777777);
                        logger.error(String.format(" Extract [sale price] data is NOT valid - %s", strItem));
                    }
                }
            }

            // 추출된 데이터가 정상인지 체크한다. 정상이 아니면 db에 넣지 않는다.
            if (!isDataEmpty(searchData)) {
                // key : product id
                searchDataMap.put(productId, searchData);
                totalExtractCount++;
            }
//            else {
//                logger.error(String.format(" 추출된 상품 데이터가 없습니다 - %s", filePath));
//                logger.error(element.outerHtml().toString());
//            }
        }

        logger.info(String.format(" %d 건의 데이터 추출 완료",searchDataMap.size()));

        return searchDataMap;
    }


    private String isSexKeywordAdd(String crawlKeyword, boolean bMan, boolean bWoman) {
        StringBuilder sb = new StringBuilder(crawlKeyword);
        if (bMan) sb.append(" 남자");
        if (bWoman) sb.append(" 여자");
        return sb.toString();
    }


    public void insertOkMall(Map<String, SearchData> sdAll) throws IOException {
        SearchDataService searchDataService = new SearchDataService();
        SearchData sd;

        for(Map.Entry<String, SearchData> entry : sdAll.entrySet()) {
            //strUrlLink = entry.getKey();
            sd = entry.getValue();

            // insert or update 타입이 없는 경우
            if (sd.getType().isEmpty()) {
                logger.warn(String.format(" UPDATED(empty) : (%s)(%s)(%s)",
                        sd.getProductId(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                unknownCount++;
            }
            else if (sd.getType().equals("insert")) {

                logger.info(String.format(" INSERTED : %s|%f|%d|%d|%s|%s",
                        sd.getProductId(),
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.insertSearchData(sd);
                insertCount++;
            }
            else if (sd.getType().equals("update")) {

                logger.info(String.format(" UPDATED : %s|%f|%d|%d|%s|%s",
                        sd.getProductId(),
                        sd.getSalePer(),
                        sd.getSalePrice(),
                        sd.getOrgPrice(),
                        sd.getProductName(),
                        sd.getContentUrl()));

                searchDataService.updateSearchData(sd);
                updateCount++;
            }
            else {
                logger.error(String.format(" data biz is not (inset or update) %d", sd.getDataId()));
            }

        }
    }


    public int checkDataCount(String path, String readEncoding) throws IOException {
        String patten = "div.brand_detail_layer p.item_title a span.prName_PrName";
        FileIO fileIO = new FileIO();
        fileIO.setPath(path);
        fileIO.setEncoding(readEncoding);

        String data = fileIO.getFileContent();
        Document doc = Jsoup.parse(data);
        Elements elements = doc.select(patten);
        return elements.size();
    }


    public Map<String, SearchData> checkSearchDataValid(
            Map<String, SearchData> allMap, Map<String, SearchData> partMap) throws Exception {

        String productId;
        SearchData searchDataPart;
        SearchData searchDataAll;
        Map<String, SearchData> newSearchDataMap = new HashMap<String, SearchData>();

        for(Map.Entry<String, SearchData> entry : partMap.entrySet()) {
            productId = entry.getKey();
            searchDataPart = entry.getValue();

            if (isDataEmpty(searchDataPart)) {
                logger.error(String.format(" Null 데이터가 있어서 skip 합니다 (%s)",
                        searchDataPart.getProductId()));
                continue;
            }

            // 기존 추출된 데이터가 이미 존재하는 경우.
            if (allMap.containsKey(productId)) {
                // 기존 데이터에서 하나 꺼내서.
                searchDataAll = allMap.get(productId);
                if (isDataEmpty(searchDataAll)) {
                    logger.error(String.format(" 기존 데이중에 Null 데이터가 있어서 skip 합니다. prdid(%s)",
                            searchDataAll.getProductId()));
                    continue;
                }

                // 동일한 데이터가 있는지 비교한다.
                if (searchDataAll.getSalePrice().equals(searchDataPart.getSalePrice())
                        && searchDataAll.getProductName().equals(searchDataPart.getProductName())
                        && searchDataAll.getOrgPrice().equals(searchDataPart.getOrgPrice())) {

                    // 동일한 데이터가 있으면 아무것도 안한다.

//                    logger.info(String.format(" SAME DATA (%s)(%d)(%d)(%s)",
//                            searchDataAll.getProductId(),
//                            searchDataAll.getSalePrice(),
//                            searchDataAll.getOrgPrice(),
//                            searchDataAll.getProductName()));
                }
                // product id는 동일하지만 필드 값이 다른경우.
                else {
                    logger.info(String.format(" UPDATE SET (%s)(%f)(%d)(%d)(%s)(%s)",
                            searchDataPart.getProductId(),
                            searchDataPart.getSalePer(),
                            searchDataPart.getSalePrice(),
                            searchDataPart.getOrgPrice(),
                            searchDataPart.getProductName(),
                            searchDataPart.getContentUrl()));

                    searchDataPart.setType("update");
                    searchDataPart.setDataStatus("U");
                    // 변경된 데이터가 있기 때문에 해당 db에 업데이트를 하기 위해 id 셋팅.
                    searchDataPart.setDataId(searchDataAll.getDataId());
                    newSearchDataMap.put(productId, searchDataPart);
                }
            }
            // 동일한 product id가 없는 경우
            else {
                logger.info(String.format(" INSERT SET %s", searchDataPart.getProductId()));
                searchDataPart.setType("insert");
                searchDataPart.setDataStatus("I");
                newSearchDataMap.put(productId, searchDataPart);
            }
        }

        return newSearchDataMap;
    }


    public String makeUrlPage(String url, int page)  {
        return String.format("%s&page=%d", url, page);
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
        int returnCode;
        long randomNum=0;
        int data_size=0;
        String strUrl;
        String crawlSavePath;
        String savePrefixPath = globalInfo.getSaveFilePath();

        // 환경 셋팅
        crawlSite.setConnectionTimeout(5000);
        crawlSite.setSocketTimeout(5000);
        crawlSite.setCrawlEncode(txtEncode);

        for(;;) {

            // page는 0부터 시작해서 추출된 데이터가 없을때까지 증가 시킨다.
            strUrl = String.format("%s&page=%d", url, page);

            // set crawling information.
            crawlSite.setCrawlUrl(strUrl);

            // Crawliing...
            returnCode = crawlSite.HttpCrawlGetDataTimeout();
            if (returnCode != 200 && returnCode != 201) {
                logger.error(String.format(" 데이터를 수집 못했음 - %s", strUrl));
                crawlErrorCount++;
                continue;
            }

            // 수집한 데이터를 파일로 저장한다.
            randomNum = random.nextInt(918277377);
            crawlSavePath = savePrefixPath + "/" + strCpName + "/" + Long.toString(randomNum) + ".html";
            // 만일 파일이름이 충돌 난다면...
            File f = new File(crawlSavePath);
            if(f.exists()) {
                logger.error(String.format(" 저장할 파일 이름이 충돌 납니다 - %s ", crawlSavePath));
                collisionFileCount++;
                continue;
            }

            crawlIO.setSaveDataInfo(crawlSite.getCrawlData(), crawlSavePath, txtEncode);
            // 크롤링한 데이터를 파일로 저장한다.
            crawlIO.executeSaveData();

            // 추출된 데이터가 없으면 page 증가를 엄추고 새로운 seed로 다시 수집하기 위해
            // 추출된 데이터가 있는지 체크한다.
            data_size = checkDataCount(crawlSavePath, txtEncode);
            if (data_size <= 0) {
                logger.info(" This seed last page : " + strUrl);
                break;
            }

            // 수집한 메타 데이터를 DB에 저장한다.
            crawlData.setSeedUrl(strUrl);
            crawlData.setCrawlDate(dateInfo.getCurrDateTime());
            crawlData.setSavePath(crawlSavePath);
            crawlData.setCpName(strCpName);
            crawlData.setCrawlKeyword(strKeyword);
            // 크롤링한 메타데이터를 db에 저장한다.
            crawlDataService.insertCrawlData(crawlData);
            logger.info(String.format(" Crawling ( %d ) %s", data_size, strUrl));

            // page를 증가 시킨다.
            page++;
            // 크롤링한 데이터 카운트.
            crawlCount++;
        }
    }


    private String getFieldData(String src, String startTag, String endTag) {
        if (src==null || startTag==null || endTag==null) return "";
        int spos = src.indexOf(startTag);
        if (spos<=0) return "";
        int epos = src.indexOf(endTag, spos);
        String tag = src.substring(spos+startTag.length(), epos);

        return tag;
    }


    public int indexingOkMall(SearchData searchData) throws IOException {

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
        sb.append("\"http://www.okmall.com").append(searchData.getContentUrl()).append("\",");

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

        // set docid
        indexUrl.append(searchData.getProductId());
        // set crawl url
        crawlSite.setCrawlUrl(indexUrl.toString());
        // set crawl url data
        crawlSite.setCrawlData(sb.toString());

        // indexing request
        returnCode = crawlSite.HttpXPUT();

        if (returnCode == 200 || returnCode == 201) {
            logger.info(String.format(" Indexing [ %d ] %s - %s",
                    returnCode,
                    searchData.getProductId(),
                    searchData.getProductName()));
        }
        else {
            logger.error(String.format(" Indexing [ %d ] %s - %s",
                    returnCode,
                    searchData.getProductId(),
                    searchData.getProductName()));
        }

        return returnCode;
    }

    public boolean isDataEmpty(SearchData sd) {
        if (sd.getOrgPrice()==null) return true;
        if (sd.getSalePrice()==null) return true;
        if (sd.getProductName()==null) return true;
        if (sd.getProductId()==null) return true;
        if (sd.getCpName()==null) return true;
        if (sd.getContentUrl()==null) return true;
        if (sd.getThumbUrl()==null) return true;

        if (sd.getProductName().isEmpty()) return true;
        if (sd.getCpName().isEmpty()) return true;
        if (sd.getContentUrl().isEmpty()) return true;
        if (sd.getThumbUrl().isEmpty()) return true;

        return false;
    }

    public static boolean isAllDigitChar(String s) {
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllFloatChar(String s) {
        for (char ch : s.toCharArray()) {
            if (!Character.isDigit(ch) && !(ch == '.')) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        OkMallProc ok = new OkMallProc();
        String s = "/product/view.html?no=115505&pID=20000677&UNI=M";
        logger.info(ok.getFieldData(s, "no=", "&"));
    }
}
