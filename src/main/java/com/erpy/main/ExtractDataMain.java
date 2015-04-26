package com.erpy.main;

import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.SearchData;
import com.erpy.dao.SearchDataService;
import com.erpy.parser.*;
import com.erpy.social.*;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * Created by baeonejune on 14. 12. 27..
 */
public class ExtractDataMain {

    private static Logger logger = Logger.getLogger(ExtractDataMain.class.getName());

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
        SnowPeak sp = new SnowPeak();
        // social
        Coopang coopang = new Coopang();
        WeMef weMef = new WeMef();
        Timon timon = new Timon();
        G9 g9 = new G9();
        GSdeal gSdeal = new GSdeal();
        LotteThanksDeal lotteThanksDeal = new LotteThanksDeal();
        HotKill hotKill = new HotKill();
        HappyVirusFrist happyVirusFrist = new HappyVirusFrist();
        HappyVirusPost happyVirusPost = new HappyVirusPost();
        HappyDeals happyDeals = new HappyDeals();



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

            if (crawlData.getCpName().equals(GlobalInfo.CP_CCAMPING)) { // 수집이 안됨.
                cc.mainExtractProcessing(cc, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_DICAMPING)) { // 확인
                di.mainExtractProcessing(di, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_SBCLUB)) { // yes
                sbclub.mainExtractProcessing(sbclub, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CAMPINGMALL)) { // 확인
                campingMall.mainExtractProcessing(campingMall, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_OKMALL)) { // 확인
                okMallProc.mainExtractProcessing(okMallProc, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_FIRST)) { // 확인
                first.mainExtractProcessing(first,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampTown)) { // 확인
                ct.mainExtractProcessing(ct,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CAMPINGON)) { // 썸네일 이슈
//                co.mainExtractProcessing(co,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Aldebaran)) { // yes
                alde.mainExtractProcessing(alde,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_OMyCamping)) { //yes
                omy.mainExtractProcessing(omy,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampI)) { //yes
                cmpi.mainExtractProcessing(cmpi,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Camping365)) {    // yes
                cp365.mainExtractProcessing(cp365,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_LeisureMan)) { // yes
                lsm.mainExtractProcessing(lsm,crawlData,allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_WeekEnders)) { // yes
                wk.mainExtractProcessing(wk, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CampingPlus)) { // yes
                cplus.mainExtractProcessing(cplus, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_CouPang)) { // yes
                coopang.mainExtractProcessing(coopang, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_WeMef)) { // yes
                weMef.mainExtractProcessing(weMef, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_Timon)) { // yes
                timon.mainExtractProcessing(timon, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_G9)) { // yes
                g9.mainExtractProcessing(g9, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_GSDeal)) { // yes
                gSdeal.mainExtractProcessing(gSdeal, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_LotteThanksDeal)) { // yes
                lotteThanksDeal.mainExtractProcessing(lotteThanksDeal, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_HotKill)) { // yes
                hotKill.mainExtractProcessing(hotKill, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_HappyVirusFirst)) { // yes
                happyVirusFrist.mainExtractProcessing(happyVirusFrist, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_HappyVirusPost)) { // yes
                happyVirusPost.mainExtractProcessing(happyVirusPost, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_HappyDeals)) { // yes
                happyDeals.mainExtractProcessing(happyDeals, crawlData, allSearchDatasMap);
            }
            else if (crawlData.getCpName().equals(GlobalInfo.CP_SnowPeak)) {
//                sp.mainExtractProcessing(sp, crawlData, allSearchDatasMap);
            }
            else {
                logger.error(String.format(" Other cp occurred!! - (%s)", crawlData.getCpName()));
            }
        }
        logger.info(" Extract end!");
    }
}
