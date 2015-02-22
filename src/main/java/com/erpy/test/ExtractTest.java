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
        String htmlContent = "<div class=\"item_box  off\" style=\"background-color:#FFFFFF;height:520px\" data-ProductNo=\"117169\">\n" +
                "<div class=\"size_layer_box\" id=\"viewLayerArea_117169\" style=\"display:none;\"><!-- 사이즈 정보 레이어 영역 --></div>\n" +
                "<div class=\"size_layer_box season_layer\" id=\"viewLayerArea1_117169\" style=\"display:none;\"><!-- 사용권장월 레이어 영역 --></div>\n" +
                "<ul>\n" +
                "<li>\n" +
                "<a href=\"javascript:;\"><img src=\"http://121.254.171.83/Images/OKmall/PC/Product/Btn/20141118/btn_product_size.png\" alt=\"현재 구매 가능한 사이즈를 볼 수 있습니다.\" name=\"getSizeInfo\" t=\"117169\"></a>\n" +
                "</li>\n" +
                "<li>\n" +
                "<img src=\"http://121.254.171.83/Images//OKmall/PC/Product/Btn/20141118/img_product_woman.png\" alt=\"여성용 상품\"></a>\n" +
                "</li>\n" +
                "</ul>\n" +
                "<div class=\"os_border\">\n" +
                "<div class=\"item_img\">\n" +
                "<div style=\"position:absolute;padding-top:13px;\">\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "<!-- 상품이미지 -->\n" +
                "<a href=\"/product/view.html?no=117169&pID=20000678&UNI=F\" ><img width=\"292\" height=\"339\" src=\"http://121.254.171.83/ProductImages/117000/img117169_middle6.JPG\" class=\"pImg\" name=\"ProductImage\" style=\"cursor: pointer; display: inline;\" data-original='http://121.254.171.83/ProductImages/117000/img117169_middle6.JPG'>\n" +
                "</a>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<p>\n" +
                "</p>\n" +
                "\n" +
                "<img alt=\"이미지 더보기\" src=\"http://121.254.171.83/Images//OKmall/PC/Product/Btn/btn_plus.gif\" class=\"zoom_ic\"  id=\"viewImageList\">\n" +
                "\n" +
                "<div class=\"gift_wrap\">\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"val_top\">\n" +
                "<p class=\"item_title\" name=\"shortProductName\"><a href=\"/product/view.html?no=117169&pID=20000678&UNI=F\" ><span class=\"prName_PrName\"><span class=\"prName_Brand\">[adidas]</span>W HT WANDERTAG (D81775) (W HT 완더탁 자켓)</span></a>\n" +
                "</p>\n" +
                "<div style=\"display:none;\" name=\"detailName\" class=\"brand_detail_layer\">\n" +
                "<p class=\"item_title\">\n" +
                "<a href=\"/product/view.html?no=117169&pID=20000678&UNI=F\"><span class=\"prName_Brand\">[adidas]</span><span class=\"prName_PrName\">W HT WANDERTAG (D81775) (W HT 완더탁 자켓)</span></a>\n" +
                "</p>\n" +
                "<p>\n" +
                "<a href=\"/product/product.html?pr_search_mode=on&brand=%BE%C6%B5%F0%B4%D9%BD%BA%28adidas%29\" class=\"brand_name\" target=\"_blank\">브랜드명:아디다스</a>\n" +
                "</p>\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "<!-- s : al_left -->\n" +
                "<div class=\"al_left\">\n" +
                "<div class=\"icon_group clearfix\">\n" +
                "<!-- s : 할인율 표시 -->\n" +
                "<div class=\"ic ic_coupon\">\n" +
                "<div class=\"icon\">25<span>.5</span></div>\n" +
                "</div>\n" +
                "<!-- e : 할인율 표시 -->\n" +
                "</div>\n" +
                "<!-- end of icon_group -->\n" +
                "\n" +
                "<div class=\"real_price\">\n" +
                "<div class=\"value_price\">\n" +
                "<span class=\"l\"><span>159,000원</span></span>\n" +
                "</div>\n" +
                "<div class=\"\n" +
                "last_price\n" +
                "\">\n" +
                "<span class=\"l\"><span>118,500<span>원</span></span> \n" +
                "<span class=\"name_price2\">(오케이몰가)</span>\n" +
                "</span>\n" +
                "<span class=\"r \"><a href=\"#\" name=\"viewPrice\" data-No=\"117169\" data-Code=\"VIPDC\"><img src=\"http://121.254.171.83/Images//OKOutdoor/PC/Common/Icons/20140508/icon_help.gif\"></a></span>\n" +
                "\n" +
                "<div style=\"display:none;width:270px;\" class=\"viewPriceLayer\" name=\"viewPriceLayer\"><!-- 가격 안내레이어 영역 --></div>\n" +
                "</div>\n" +
                "<div class=\"\n" +
                "plus_price\">\n" +
                "<span class=\"l\"><span>116,100<span>원</span></span> (우수회원 맞춤가)</span>\n" +
                "<span class=\"r\"><a href=\"#\" name=\"viewPrice\" data-No=\"117169\" data-Code=\"VIPDC_ex\"><img src=\"http://121.254.171.83/Images//OKOutdoor/PC/Common/Icons/20140508/icon_help.gif\"></a></span>\n" +
                "\n" +
                "<div style=\"display:none;width:270px;\" class=\"viewPriceLayer\" name=\"viewPriceLayer\"><!-- 가격 안내레이어 영역 --></div>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
                "<!-- e : al_left -->\n" +
                "\n" +
                "</div>";

        Document docu = Jsoup.parse(htmlContent);
        Elements elementsLink = docu.select("div.item_box");
        int count=0;
        for (Element elink : elementsLink) {
            String strItem = elink.outerHtml();
            Document document = Jsoup.parse(elink.outerHtml());
            Elements elements = document.select("ul li img");
            for (Element element : elements) {
//                logger.info(element.outerHtml());
                logger.info(element.attr("alt").trim());
            }
        }

        System.out.println("end");
    }
}
