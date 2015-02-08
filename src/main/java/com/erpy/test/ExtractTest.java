package com.erpy.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by baeonejune on 15. 2. 8..
 */
public class ExtractTest {
    public static void main (String args[]) throws Exception {
        String htmlContent = "<div class=\"real_price\">" +
                "<div class=\"value_price\">" +
                "<span class=\"l\"><span>285,000원</span></span>" +
                "</div>" +
                "<div class=\"" +
                "last_price" +
                "\">" +
                "<span class=\"l\"><span>129,000<span>원</span></span> " +
                "<span class=\"name_price2\">(오케이몰가)</span>" +
                "</span>" +
                "<span class=\"r \"><a href=\"#\" name=\"viewPrice\" data-No=\"113353\" data-Code=\"VIPDC\"><img src=\"http://121.254.171.83/Images//OKOutdoor/PC/Common/Icons/20140508/icon_help.gif\"></a></span>" +
                "" +
                "<div style=\"display:none;width:270px;\" class=\"viewPriceLayer\" name=\"viewPriceLayer\"><!-- 가격 안내레이어 영역 --></div>" +
                "</div>" +
                "<div class=\"" +
                "plus_price\">" +
                "<span class=\"l\"><span>116,100<span>원</span></span> (우수회원 맞춤가)</span>" +
                "<span class=\"r\"><a href=\"#\" name=\"viewPrice\" data-No=\"113353\" data-Code=\"VIPDC_ex\"><img src=\"http://121.254.171.83/Images//OKOutdoor/PC/Common/Icons/20140508/icon_help.gif\"></a></span>" +
                "" +
                "<div style=\"display:none;width:270px;\" class=\"viewPriceLayer\" name=\"viewPriceLayer\"><!-- 가격 안내레이어 영역 --></div>" +
                "</div>" +
                "</div>";

        Document docu = Jsoup.parse(htmlContent);
        Elements elementsLink = docu.select("div.real_price div.last_price span.l span");
        int count=0;
        for (Element elink : elementsLink) {
            String strItem = elink.text();
            strItem = strItem.replace("%", "").replace(" ", "");
            System.out.println(String.format(">> Sale Per : (%d)%s", count, strItem));
            count++;
        }

        System.out.println("end");
    }
}
