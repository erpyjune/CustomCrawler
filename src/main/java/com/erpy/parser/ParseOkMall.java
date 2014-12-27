package com.erpy.parser;

import com.erpy.dao.SearchData;
import com.erpy.io.FileIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ParseOkMall {
    private String filePath=null;
    private String txtEncode="euc-kr";
    private Integer size=0;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void extractOkMall() throws IOException {
        FileIO fileIO = new FileIO();
        fileIO.setEncoding(this.txtEncode);
        fileIO.setPath(this.filePath);
        Elements elements;
        Elements elementsLink;
        Document document;
        String strItem;

        SearchData searchDataG = new SearchData();
        Map<Integer, SearchData> searchDataMap = new HashMap<Integer, SearchData>();

        if (filePath==null) {
            System.out.println("(ERROR) filePath is null !!");
            System.exit(0);
        }

        fileIO.setEncoding(txtEncode);
        String htmlContent = fileIO.getFileContent();
        Document doc = Jsoup.parse(htmlContent);

        //////////////////////////////////////////
        // 1) content link
        // 2) image link
        size = 0;
        elements = doc.select("div.item_box div.os_border");
        for (Element element : elements) {
            document = Jsoup.parse(element.outerHtml());
            // link
            elementsLink = document.select("a");
            for (Element elink : elementsLink) {
                strItem = elink.attr("href");
                System.out.println(strItem);
            }
            // thumb
            elementsLink = document.select("a img");
            for (Element elink : elementsLink) {
                strItem = elink.attr("data-original");
                System.out.println(strItem);
            }
            System.out.println("-----------------------------");
            size++;
        }

        //////////////////////////////////////////
        // 1) product name
        size = 0;
        elements = doc.select("div.item_box div.val_top");
        for (Element element : elements) {
            document = Jsoup.parse(element.outerHtml());
            // link
            elementsLink = document.select("div.brand_detail_layer p.item_title a");
            for (Element elink : elementsLink) {
                strItem = elink.text();
                System.out.println(strItem);
            }
            System.out.println("-----------------------------");
            size++;
        }

        //////////////////////////////////////////
        // 1) sale per
        // 2) org  price
        // 3) sale price
        size = 0;
        elements = doc.select("div.item_box div.al_left");
        for (Element element : elements) {
            document = Jsoup.parse(element.outerHtml());
            // sale per
            elementsLink = document.select("div.icon_group.clearfix div.ic.ic_coupon p");
            for (Element elink : elementsLink) {
                strItem = elink.text();
                System.out.println(strItem);
            }
            // org price
            elementsLink = document.select("div.real_price div.real_price01 span.r");
            for (Element elink : elementsLink) {
                strItem = elink.text();
                System.out.println(strItem);
            }
            // sale price
            elementsLink = document.select("div.real_price div.real_price03.f_c16.fb span.r");
            for (Element elink : elementsLink) {
                strItem = elink.text();
                System.out.println(strItem);
            }

            System.out.println("-----------------------------");
            size++;
        }
    }
}
