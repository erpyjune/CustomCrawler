package com.erpy.test;

import com.erpy.io.FileIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class JsoupTest {
    public static void main(String args[]) throws IOException {
        int total=0;
        FileIO fileIO = new FileIO();
        fileIO.setEncoding("euc-kr");
        fileIO.setPath("/Users/baeonejune/work/social_shop/crawl_data/okmall/5030766.html");

        String htmlContent = fileIO.getFileContent();
        Document doc = Jsoup.parse(htmlContent);

        // image.
/*        Elements elements = doc.select("div.item_box div.os_border");
        for (Element element : elements) {
            //System.out.println(".tagName : " + element.tagName());
            //System.out.println(".text : " + element.text());
            //System.out.println(".attr(src) : " + element.attr("src"));
            System.out.println(element.outerHtml());
            System.out.println("-----------------------------");
            total++;
        }*/

        // brand, title.
/*        Elements elements = doc.select("div.item_box div.val_top");
        for (Element element : elements) {
            //System.out.println(".tagName : " + element.tagName());
            //System.out.println(".text : " + element.text());
            //System.out.println(".attr(src) : " + element.attr("src"));
            System.out.println(element.outerHtml());
            System.out.println("-----------------------------");
            total++;
        }*/

        // price
        Document price_doc;
        Elements elements = doc.select("div.item_box[data-ProductNo]");
        for (Element element : elements) {
            price_doc = Jsoup.parse(element.outerHtml());
            Elements price_elements = price_doc.select("div.real_price01 > span.r");
            for (Element price_elmt : price_elements) {
                System.out.println(".text : " + price_elmt.text());
            }
            System.out.println("-----------------------------");
            total++;
        }

        System.out.println("end --> " + total);
    }
}
