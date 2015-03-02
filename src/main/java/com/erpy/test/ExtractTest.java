package com.erpy.test;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by baeonejune on 15. 2. 8..
 */
public class ExtractTest {
    private static Logger logger = Logger.getLogger(ExtractTest.class.getName());
    public static void main (String args[]) throws Exception {
        String htmlContent = "<td align=center valign=top width=\"25%\">\n" +
                "\t<div style=\"width:180px;height:180px;border-bottom-width:1px; border-top-width:1px; border-left-width:1px; border-right-width:1px; border-style:solid; border-color:#c1c1c1;\">\n" +
                "\t\t<a href=\"/shop/goods/goods_view.php?&goodsno=107454&category=002007\">\n" +
                "\t\t\t<img src='../data/goods/1394420465_m_0.jpg' width=180 onerror=this.src='/shop/data/skin/siesta_creamy_C/img/common/noimg_300.gif' />\n" +
                "\t\t</a>\n" +
                "\t</div>\n" +
                "\t<div align=\"left\" style=\"width:180px;height:50px;padding-top:3px;\">\n" +
                "\t\t<a href=\"/shop/goods/goods_view.php?&goodsno=107454\">면텐트 1인용 캔버스텐트/캠핑텐트 -지코드프라임\n" +
                "\t\t</a>\n" +
                "\t<div align=\"left\" style=\"padding:5\"></div>\n" +
                "\t<div align=\"left\" style=\"height:10px;\">\n" +
                "\t\t<strike>480,000</strike>\n" +
                "\t</div>\n" +
                "\t<div style=\"color:#ff4e00;font-size:16px;width:180pxheight:18px;\" align=\"left\">\n" +
                "\t\t<b>480,000원</b>\n" +
                "\t</div>\n" +
                "</td>";

        Document docu = Jsoup.parse(htmlContent);
        Elements elementsLink = docu.select("td");
        int count=0;
        for (Element elink : elementsLink) {
            String strItem = elink.outerHtml();
            logger.info(" " + strItem);
            logger.info(" ------------------------------------------");
//            Document document = Jsoup.parse(elink.outerHtml());
//            Elements elements = document.select("ul li img");
//            for (Element element : elements) {
//                logger.info(element.outerHtml());
//                logger.info(element.attr("alt").trim());
//            }
        }

        System.out.println("end");
    }
}
