package com.erpy.main;

import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.CampingMall;
import com.erpy.parser.First;
import com.erpy.parser.OkMallProc;
import com.erpy.parser.SB;
import com.erpy.utils.GlobalInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlMain {

    private static Logger logger = Logger.getLogger(CrawlMain.class.getName());

    public static void main(String args[]) throws IOException {

        Seed seed;
        String strKeyword;
        String strUrl;
        String strCpName;
        int seedCount=0;

        SeedService seedService = new SeedService();

        // all crawl_data delete.
        CrawlDataService crawlDataService = new CrawlDataService();
        crawlDataService.deleteCrawlDataAll();
        logger.info(" crawl_data table delete all");

        // get crawl seeds.
        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            strKeyword = seed.getKeyword();
            strUrl     = seed.getUrl();
            strCpName  = StringUtils.trim(seed.getCpName());

            if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
                SB cp = new SB();
                cp.setTxtEncode("utf-8");
                cp.crawlData(strUrl, strKeyword, strCpName);
            }
//            else if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
//                CampingMall cp = new CampingMall();
//                cp.setTxtEncode("euc-kr");
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            }
//            else if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
//                OkMallProc okMallProc = new OkMallProc();
//                okMallProc.setTxtEncode("euc-kr");
//                // 데이터 수집 시작..
//                okMallProc.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_FIRST)) {
//                First first = new First();
//                first.setTxtEncode("utf-8");
//                first.crawlData(strUrl, strKeyword, strCpName);
//            }
        }
    }
}
