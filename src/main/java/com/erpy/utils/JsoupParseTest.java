package com.erpy.utils;

import com.erpy.io.FileIO;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by baeonejune on 14. 12. 26..
 */
public class JsoupParseTest {
    public static void main(String args[]) throws IOException {
        String test_path = "/Users/baeonejune/work/social_shop/crawl_data/okmall/5030766.html";
        String htmlContent=null;
        int total[] = new int[10];
        List<String> listLink = new LinkedList<String>();
        List<String> listImg  = new LinkedList<String>();
        List<String> listTitle  = new LinkedList<String>();
        List<String> listBrand  = new LinkedList<String>();
        FileIO fileIO = new FileIO();

        System.out.println("input : " + test_path);

        fileIO.setEncoding("euc-kr");
        fileIO.setPath(test_path);
        htmlContent = fileIO.getFileContent();
        //Document doc = Jsoup.connect(url).get();
        Document doc = Jsoup.parse(htmlContent);

        //Elements datas = doc.select("div.os_border div.item_img a[href]");
        //Elements datas = doc.select("a[href]");


/*
       Element element = doc.select("div.login h2 img").first();
        System.out.println("el.tagName : "+element.tagName());
        System.out.println("el.attr(src) : "+element.attr("src"));
        System.out.println("el.attr(alt) : "+element.attr("alt"));
        System.out.println("el.text : "+element.text());*/


        // extract link.
        Elements links = doc.select("div.os_border div.item_img a");
        for (Element link: links) {
//            System.out.println(".tagName : " + link.tagName());
//            System.out.println(".text : " + link.text());
//            System.out.println(".attr(href) : " + link.attr("href"));
//            System.out.println("-----------------------------");
            listLink.add(link.attr("href"));
            total[0]++;
        }

        // extract link.
        Elements images = doc.select("div.os_border div.item_img a img.pImg").attr("name", "ProductImage");
        for (Element image: images) {
//            System.out.println(".tagName : " + image.tagName());
//            System.out.println(".text : " + image.text());
//            System.out.println(".attr(href) : " + image.attr("data-original"));
//            System.out.println("-----------------------------");
            listImg.add(image.attr("data-original"));
            total[1]++;
        }

        // extract brand name.
        Elements brands = doc.select("div.val_top div.brand_detail_layer p.item_title a span.prName_Brand");
        for (Element brand: brands) {
//            System.out.println(".tagName : " + brand.tagName());
//            System.out.println(".text : " + brand.text());
//            System.out.println(".attr(href) : " + brand.attr("href"));
//            System.out.println("-----------------------------");
            listBrand.add(brand.tagName());
            total[2]++;
        }

        // extract title.
        Elements titles = doc.select("div.val_top div.brand_detail_layer p.item_title a");
        for (Element title: titles) {
//            System.out.println(".tagName : " + title.tagName());
//            System.out.println(".text : " + title.text());
//            System.out.println(".attr(href) : " + title.attr("href"));
//            System.out.println("-----------------------------");
            listTitle.add(title.tagName());
            total[3]++;
        }

        // extract list price.
        Elements prices = doc.select("div.al_left div.real_price div.real_price01 span.r");
        for (Element price: prices) {
//            System.out.println(".tagName : " + price.tagName());
//            System.out.println(".text : " + price.text());
//            System.out.println(".attr(href) : " + price.attr("href"));
//            System.out.println("-----------------------------");
//            listTitle.add(price.tagName());
            total[4]++;
        }

        // extract sale price.
        Elements sale_prices = doc.select("div.al_left > div.real_price > div.real_price03.f_c16.fb > span.r");
        for (Element sale_price: sale_prices) {
            System.out.println(".tagName : " + sale_price.tagName());
            System.out.println(".text : " + sale_price.text());
            System.out.println(".attr(href) : " + sale_price.attr("href"));
            System.out.println("outerHtml : "+sale_price.outerHtml());
            System.out.println("-----------------------------");
            listTitle.add(sale_price.tagName());
            total[5]++;
        }

        for (int i=0; i<6; i++) {
            System.out.println("total[" + i + "] : " + total[i]);
        }


//        Elements links = doc.select("a[href]");
//        Elements media = doc.select("[src]");
//        Elements imports = doc.select("link[href]");
//
//        print("\nMedia: (%d)", media.size());
//        for (Element src : media) {
//            if (src.tagName().equals("img"))
//                print(" * %s: <%s> %sx%s (%s)",
//                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//                        trim(src.attr("alt"), 20));
//            else
//                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
//        }
//
//        print("\nImports: (%d)", imports.size());
//        for (Element link : imports) {
//            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
//        }
//
//        print("\nLinks: (%d)", links.size());
//        for (Element link : links) {
//            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
//        }

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}
