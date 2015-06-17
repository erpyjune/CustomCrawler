package com.erpy.main;

import com.erpy.dao.ThumbnailData;
import com.erpy.thumbnail.ThumbnailProc;
import com.erpy.thumbnail.ThumbnailProcData;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

/**
 * Created by baeonejune on 15. 6. 13..
 */
public class CrawlThumbOutdoor {
    private static Logger logger = Logger.getLogger("CrawlThumbOutdoor");
    public static void main(String[] args) throws Exception {
        ThumbnailProc thumbnailProc = new ThumbnailProc();
        ThumbnailProcData thumbnailProcData = new ThumbnailProcData();
        boolean isAllData=false;

        if (args.length==0) {
            logger.error(" USAGE: need cp_name isAllUpdate");
            System.exit(-1);
        }

        String cpName = args[0];
        String allData = args[1];
        if (allData.length()==0) {
            isAllData = false;
        }

        if ("all".equals(allData) || "ALL".equals(allData)) {
            isAllData = true;
        } else {
            isAllData = false;
        }

        thumbnailProcData.setCpName(cpName);
        thumbnailProcData.setIsAllDataCrawl(isAllData);
        thumbnailProcData.setHtmlCrawlConnectionTimeout(5000);
        thumbnailProcData.setHtmlCrawlReadTimeout(10000);
        thumbnailProcData.setSavePathPrefix("/Users/baeonejune/work/SummaryNode/images");

        if (cpName.equals(GlobalInfo.CP_AirMT)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.airmt.net");
            thumbnailProcData.setHostReferer("http://www.airmt.net");
            thumbnailProcData.setHostDomain("www.airmt.net");

            thumbnailProcData.setParserGroupSelect("div[style=\"padding-bottom:10\"]");
            thumbnailProcData.setParserSkipPattern("");
            thumbnailProcData.setParserDocumentSelect("span img");
            thumbnailProcData.setReplacePatternFindData("../data/goods");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_TongOutdoor.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.EUCKR);
            thumbnailProcData.setPrefixHostThumbUrl("http://tongoutdoor.com");
            thumbnailProcData.setHostReferer("http://tongoutdoor.com");
            thumbnailProcData.setHostDomain("tongoutdoor.com");

            thumbnailProcData.setParserGroupSelect("div[style=\"padding-bottom:10\"]");
            thumbnailProcData.setParserSkipPattern("");
            thumbnailProcData.setParserDocumentSelect("span img");
            thumbnailProcData.setReplacePatternFindData("../data/goods");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Gogo337.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.EUCKR);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.gogo337.co.kr");
            thumbnailProcData.setHostReferer("http://www.gogo337.co.kr");
            thumbnailProcData.setHostDomain("www.gogo337.co.kr");

            thumbnailProcData.setParserGroupSelect("td[style=\"padding:5 0 5 0\"]");
            thumbnailProcData.setParserSkipPattern("/shopimages/");
            thumbnailProcData.setParserDocumentSelect("img.detail_image");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_SBCLUB.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.sbclub.co.kr");
            thumbnailProcData.setHostReferer("http://www.sbclub.co.kr");
            thumbnailProcData.setHostDomain("www.sbclub.co.kr");

            thumbnailProcData.setParserGroupSelect("div.view");
            thumbnailProcData.setParserSkipPattern("/goods/");
            thumbnailProcData.setParserDocumentSelect("div img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_OKMALL.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.okmall.com");
            thumbnailProcData.setHostDomain("www.okmall.com");

            thumbnailProcData.setParserGroupSelect("div.photo_wrap");
            thumbnailProcData.setParserSkipPattern("width=\"500\"");
            thumbnailProcData.setParserDocumentSelect("div.img img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_FIRST.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.chocammall.co.kr");
            thumbnailProcData.setHostReferer("http://www.chocammall.co.kr");
            thumbnailProcData.setHostDomain("www.chocammall.co.kr");

            thumbnailProcData.setParserGroupSelect("div.img_area");
            thumbnailProcData.setParserSkipPattern("style=\"width:400px; height:400px;\"");
            thumbnailProcData.setParserDocumentSelect("a img[style=\"width:400px; height:400px;\"]");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_DICAMPING.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.dicamping.co.kr");
            thumbnailProcData.setHostReferer("http://www.dicamping.co.kr");
            thumbnailProcData.setHostDomain("www.dicamping.co.kr");

            thumbnailProcData.setParserGroupSelect("div.thumb");
            thumbnailProcData.setParserSkipPattern("id=\"lens_img\"");
            thumbnailProcData.setParserDocumentSelect("img.detail_image");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Camping365.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.camping365.co.kr");
            thumbnailProcData.setHostReferer("http://www.camping365.co.kr");
            thumbnailProcData.setHostDomain("www.camping365.co.kr");

            thumbnailProcData.setParserGroupSelect("div[style=\"padding-bottom:10\"]");
            thumbnailProcData.setParserSkipPattern("goods_popup_large");
            thumbnailProcData.setParserDocumentSelect("span img");
            thumbnailProcData.setReplacePatternFindData("../data");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CAMPINGMALL.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.campingmall.co.kr");
            thumbnailProcData.setHostReferer("http://www.campingmall.co.kr");
            thumbnailProcData.setHostDomain("www.campingmall.co.kr");

            thumbnailProcData.setParserGroupSelect("table[width=\"500\"]");
            thumbnailProcData.setParserSkipPattern("id=\"objImg\"");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("../data");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Totooutdoor.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.totooutdoor.com");
            thumbnailProcData.setHostDomain("www.totooutdoor.com");

            thumbnailProcData.setParserGroupSelect("div.pic");
            thumbnailProcData.setParserSkipPattern("src=\"http://img.totooutdoor.com/images");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_WeekEnders.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.weekenders.co.kr");
            thumbnailProcData.setHostDomain("www.weekenders.co.kr");

            thumbnailProcData.setParserGroupSelect("div.keyImg");
            thumbnailProcData.setParserSkipPattern("src=\"http://www.weekenders.co.kr/web/product");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Starus.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://dkmountain.com");
            thumbnailProcData.setHostReferer("http://dkmountain.com");
            thumbnailProcData.setHostDomain("dkmountain.com");

            thumbnailProcData.setParserGroupSelect("td[height=\"52\"]");
            thumbnailProcData.setParserSkipPattern("/DATAS/es_shop_product");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_LeisureMan.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.leisureman.co.kr");
            thumbnailProcData.setHostDomain("www.leisureman.co.kr");

            thumbnailProcData.setParserGroupSelect("div.keyImg");
            thumbnailProcData.setParserSkipPattern("src=\"http://www.leisureman.co.kr/web/product");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CAMPINGON.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.campingon.co.kr");
            thumbnailProcData.setHostReferer("http://www.campingon.co.kr");
            thumbnailProcData.setHostDomain("www.campingon.co.kr");

            thumbnailProcData.setParserGroupSelect("div[style=\"padding-bottom:10\"]");
            thumbnailProcData.setParserSkipPattern("goto_large_popup");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("../data");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Niio.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.EUCKR);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.niio.co.kr/");
            thumbnailProcData.setHostReferer("http://www.niio.co.kr");
            thumbnailProcData.setHostDomain("www.niio.co.kr");

            thumbnailProcData.setParserGroupSelect("td[width=\"100%\"]");
            thumbnailProcData.setParserSkipPattern("shop2/p_image");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CCAMPING.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.EUCKR);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.ccamping.co.kr");
            thumbnailProcData.setHostReferer("http://www.ccamping.co.kr");
            thumbnailProcData.setHostDomain("www.ccamping.co.kr");

            thumbnailProcData.setParserGroupSelect("img#objImg");
            thumbnailProcData.setParserSkipPattern("goods");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("../data");
            thumbnailProcData.setReplacePatternSource("../data");
            thumbnailProcData.setReplacePatternDest("/shop/data");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CampTown.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.EUCKR);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.camptown.co.kr");
            thumbnailProcData.setHostReferer("http://www.camptown.co.kr");
            thumbnailProcData.setHostDomain("www.camptown.co.kr");

            thumbnailProcData.setParserGroupSelect("div.slides_container");
            thumbnailProcData.setParserSkipPattern("goods");
            thumbnailProcData.setParserDocumentSelect("img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_Aldebaran.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.adbr.co.kr");
            thumbnailProcData.setHostDomain("www.adbr.co.kr");

            thumbnailProcData.setParserGroupSelect("div.keyImg");
            thumbnailProcData.setParserSkipPattern("");
            thumbnailProcData.setParserDocumentSelect("a img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CampingPlus.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.camping-plus.co.kr");
            thumbnailProcData.setHostReferer("http://www.camping-plus.co.kr");
            thumbnailProcData.setHostDomain("www.camping-plus.co.kr");

            thumbnailProcData.setParserGroupSelect("table[width=\"100%\"]");
            thumbnailProcData.setParserSkipPattern("name=\"mainImage\"");
            thumbnailProcData.setParserDocumentSelect("tr td img[width=\"300\"]");
            thumbnailProcData.setReplacePatternFindData("./shop_image/");
            thumbnailProcData.setReplacePatternSource("./shop_image/");
            thumbnailProcData.setReplacePatternDest("/mall/shop_image/");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CampSchule.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.campschule.co.kr");
            thumbnailProcData.setHostDomain("www.campschule.co.kr");

            thumbnailProcData.setParserGroupSelect("table[width=\"300\"]");
            thumbnailProcData.setParserSkipPattern("javascript:image_zoom");
            thumbnailProcData.setParserDocumentSelect("tr td img[width=\"300\"]");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CampingAmigo.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("http://www.campingamigo.com");
            thumbnailProcData.setHostReferer("http://www.campingamigo.com");
            thumbnailProcData.setHostDomain("www.campingamigo.com");

            thumbnailProcData.setParserGroupSelect("div[style=\"padding-bottom:10\"]");
            thumbnailProcData.setParserSkipPattern("goods_popup_large");
            thumbnailProcData.setParserDocumentSelect("span img");
            thumbnailProcData.setReplacePatternFindData("../data/goods");
            thumbnailProcData.setReplacePatternSource("../data/goods");
            thumbnailProcData.setReplacePatternDest("/shop/data/goods");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_CampI.equals(cpName)) {
            thumbnailProcData.setParserType(1);
            thumbnailProcData.setHtmlCrawlEncoding(GlobalInfo.UTF8);
            thumbnailProcData.setPrefixHostThumbUrl("");
            thumbnailProcData.setHostReferer("http://www.campi.kr");
            thumbnailProcData.setHostDomain("www.campi.kr");

            thumbnailProcData.setParserGroupSelect("table[width=\"300\"]");
            thumbnailProcData.setParserSkipPattern("javascript:image_zoom");
            thumbnailProcData.setParserDocumentSelect("tr td a img");
            thumbnailProcData.setReplacePatternFindData("");
            thumbnailProcData.setReplacePatternSource("");
            thumbnailProcData.setReplacePatternDest("");

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else if (GlobalInfo.CP_GoodCamping.equals(cpName)) {
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

            thumbnailProc.thumbnailProcessing(thumbnailProcData);
        } else {
            logger.error(" Failure cp is not known !! ");
        }

        logger.info(" Processing is normally end!!");
    }
}
