package com.erpy.main;

import com.erpy.parser.*;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

/**
 * Created by baeonejune on 15. 5. 17..
 */
public class CrawlThumb {
    private static Logger logger = Logger.getLogger("CrawlThumb");
    public static void main(String[] args) throws Exception {
        AirMT airMT = new AirMT();
        TongOutdoor tongOutdoor = new TongOutdoor();
        Gogo337 gogo337 = new Gogo337();
        SB sb = new SB();
        OkMallProc ok = new OkMallProc();
        First first = new First();
        DICamping diCamping = new DICamping();
        Camping365 camping365 = new Camping365();
        CampingMall campingMall = new CampingMall();
        TotoOutdoor totoOutdoor = new TotoOutdoor();
        WeekEnders weekEnders = new WeekEnders();
        Starus starus = new Starus();
        LeisureMan leisureMan = new LeisureMan();
        CampingOn campingOn = new CampingOn();
        Niio niio = new Niio();
        CCamping cCamping = new CCamping();
        CampTown campTown = new CampTown();
        boolean isAllData=false;


        if (args.length==0) {
            logger.error(" USAGE: need cp_name");
            System.exit(-1);
        }

        String cpName = args[0];
        String allData = args[1];
        if (allData.length()==0) {
            isAllData = false;
        } else if (allData.equals("all") || allData.equals("ALL")) {
            isAllData = true;
        }



        if (cpName.equals("all") || cpName.equals("ALL")) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT, isAllData);
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor, isAllData);
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337, isAllData);
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB, isAllData);
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL, isAllData);
            first.thumbnailProcessing(GlobalInfo.CP_FIRST, isAllData);
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING, isAllData);
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365, isAllData);
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL, isAllData);
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor, isAllData);
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders, isAllData);
            starus.thumbnailProcessing(GlobalInfo.CP_Starus, isAllData);
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan, isAllData);
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON, isAllData);
            niio.thumbnailProcessing(GlobalInfo.CP_Niio, isAllData);
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING, isAllData);
            campTown.thumbnailProcessing(GlobalInfo.CP_CampTown, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_AirMT)) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_TongOutdoor)) {
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_Gogo337)) {
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_SBCLUB)) {
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_OKMALL)) {
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_FIRST)) {
            first.thumbnailProcessing(GlobalInfo.CP_FIRST, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_DICAMPING)) {
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_Camping365)) {
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_Totooutdoor)) {
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_WeekEnders)) {
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_Starus)) {
            starus.thumbnailProcessing(GlobalInfo.CP_Starus, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_LeisureMan)) {
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGON)) {
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_Niio)) {
            niio.thumbnailProcessing(GlobalInfo.CP_Niio, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_CCAMPING)) {
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING, isAllData);
        }
        else if (cpName.equals(GlobalInfo.CP_CampTown)) {
            campTown.thumbnailProcessing(GlobalInfo.CP_CampTown, isAllData);
        }
        else {
            logger.error(" Unknown cp_name !!");
        }

        logger.info(" Processing is normally end!!");
    }
}
