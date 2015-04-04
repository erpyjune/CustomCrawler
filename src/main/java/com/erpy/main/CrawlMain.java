package com.erpy.main;

import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.*;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;


/**
 * Created by baeonejune on 14. 12. 21..
 */
public class CrawlMain {

    private static Logger logger = Logger.getLogger(CrawlMain.class.getName());

    public static void main(String args[]) throws Exception {

        Seed seed;
        String strKeyword;
        String strUrl;
        String strCpName;
        int seedCount = 0;

        SeedService seedService = new SeedService();

        // all crawl_data delete.
        CrawlDataService crawlDataService = new CrawlDataService();
        crawlDataService.deleteCrawlDataAll();
        logger.info(" crawl_data table delete all");

        // get crawl seeds.
        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed) iterator.next();
            strKeyword = seed.getKeyword();
            strUrl = seed.getUrl();
            strCpName = seed.getCpName().trim();

//            if (strCpName.equals(GlobalInfo.CP_CCAMPING)) {
//                CCamping cp = new CCamping();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_DICAMPING)) {
//                DICamping cp = new DICamping();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_SBCLUB)) {
//                SB cp = new SB();
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
//                CampingMall cp = new CampingMall();
//                cp.setTxtEncode("euc-kr");
//                cp.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_OKMALL)) {
//                OkMallProc okMallProc = new OkMallProc();
//                okMallProc.setTxtEncode("euc-kr");
//                okMallProc.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_FIRST)) {
//                First first = new First();
//                first.setTxtEncode("utf-8");
//                first.crawlData(strUrl, strKeyword, strCpName);
//            } else if (strCpName.equals(GlobalInfo.CP_CAMPINGON)) {
//                CampingOn cp = new CampingOn();
//                cp.crawlData(strUrl, strKeyword, strCpName);
            if (strCpName.equals(GlobalInfo.CP_CampTown)) {
                CampTown cp = new CampTown();
                cp.crawlData(strUrl, strKeyword, strCpName);
            }
        }

        logger.info(" End crawling !!");
    }
}
