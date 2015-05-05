package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.*;
import com.erpy.social.*;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 15. 5. 4..
 */
public class ExtractOutdoor {
    private static Logger logger = Logger.getLogger("ExtractOutdoor");

    ///////////////////////////////////////////////////////////////////
    // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
    // key = cpName + productId
    ///////////////////////////////////////////////////////////////////
    private static Map<String, SearchData> getAllProductKey() throws Exception {
        Map<String, SearchData> allSearchDatasMap = new HashMap<String, SearchData>();
        SearchDataService searchDataService = new SearchDataService();

        List<SearchData> searchDataList = searchDataService.getAllSearchDatas();
        Iterator searchDataListIt = searchDataList.iterator();
        SearchData sd;
        int existCount=0;

        while (searchDataListIt.hasNext()) {
            sd = (SearchData) searchDataListIt.next();
            if (allSearchDatasMap.containsKey(sd.getProductId())) {
                existCount++;
            }
            logger.debug(String.format(" All DB Set Key(%s%s)", sd.getProductId(), sd.getCpName()));
            allSearchDatasMap.put(sd.getProductId() + sd.getCpName(), sd);
        }
        logger.debug(String.format(" 기존 모든 데이터 크기 - Total(%d), Exist(%d)", allSearchDatasMap.size(), existCount));
        return allSearchDatasMap;
    }

    public static void main(String args[]) throws Exception {
        CrawlData crawlData;
        CrawlDataService crawlDataService = new CrawlDataService();

        // cp
        First first = new First();
        OkMallProc okMallProc = new OkMallProc();
        CampingMall campingMall = new CampingMall();
        SB sbclub = new SB();
        DICamping di = new DICamping();
        CCamping cc = new CCamping();
        CampingOn co = new CampingOn();
        CampTown ct = new CampTown();
        Aldebaran alde = new Aldebaran();
        OMyCamping omy = new OMyCamping();
        CampI cmpi = new CampI();
        Camping365 cp365 = new Camping365();
        LeisureMan lsm = new LeisureMan();
        WeekEnders wk = new WeekEnders();
        CampingPlus cplus = new CampingPlus();
        Starus starus = new Starus();
        CampSchule campSchule = new CampSchule();
        TongOutdoor tong = new TongOutdoor();
        AirMT airMT = new AirMT();



        String argsCPname="";
        if (args.length > 0) {
            argsCPname = args[0];
        }

        // db에 있는 검색 데이터를 모두 읽어와서 map에 저장한다.
        Map<String, SearchData> allSearchDatasMap = getAllProductKey();

        // crawling한 원본 데이터를 db에서 하나씩 가져온다.
        Map<String, SearchData> searchDataMap = new HashMap<String, SearchData>();
        List<CrawlData> crawlDataList = crawlDataService.getAllCrawlDatas();
        Iterator iterator = crawlDataList.iterator();
        while (iterator.hasNext()) {

            crawlData = (CrawlData) iterator.next();

            if (argsCPname.length()>0) {
                if (!crawlData.getCpName().equals(argsCPname)) continue;
            }

            if (crawlData.getCpName().equals(GlobalInfo.CP_CCAMPING)) {
                cc.mainExtractProcessing(cc, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_DICAMPING)) {
                di.mainExtractProcessing(di, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_SBCLUB)) {
                sbclub.mainExtractProcessing(sbclub, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CAMPINGMALL)) {
                campingMall.mainExtractProcessing(campingMall, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_OKMALL)) {
                okMallProc.mainExtractProcessing(okMallProc, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_FIRST)) {
                first.mainExtractProcessing(first,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampTown)) {
                ct.mainExtractProcessing(ct,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CAMPINGON)) { // Image Forbidden
//                co.mainExtractProcessing(co,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Aldebaran)) {
                alde.mainExtractProcessing(alde,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_OMyCamping)) {
                omy.mainExtractProcessing(omy,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampI)) {
                cmpi.mainExtractProcessing(cmpi,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Camping365)) { // Image Forbidden
//                cp365.mainExtractProcessing(cp365,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_LeisureMan)) {
                lsm.mainExtractProcessing(lsm,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_WeekEnders)) {
                wk.mainExtractProcessing(wk, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampingPlus)) {
                cplus.mainExtractProcessing(cplus, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Starus)) {
                starus.mainExtractProcessing(starus, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampSchule)) {
                campSchule.mainExtractProcessing(campSchule, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_TongOutdoor)) {
                tong.mainExtractProcessing(tong, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_AirMT)) {
                airMT.mainExtractProcessing(airMT, crawlData, allSearchDatasMap);
            }
            else {
                logger.error(String.format(" Other cp occurred!! - (%s)", crawlData.getCpName()));
            }
        }
        logger.info(" Extract end!");
    }
}
