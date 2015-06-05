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
        String cpName="";
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


        if (args.length==0) {
            logger.error(" USAGE: need cp_name");
            System.exit(-1);
        }

        cpName = args[0];

        boolean allData = false;

        if (cpName.equals("all") || cpName.equals("ALL")) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT, allData);
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor, allData);
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337, allData);
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB, allData);
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL, allData);
            first.thumbnailProcessing(GlobalInfo.CP_FIRST, allData);
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING, allData);
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365, allData);
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL, allData);
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor, allData);
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders, allData);
            starus.thumbnailProcessing(GlobalInfo.CP_Starus, allData);
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan, allData);
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON, allData);
            niio.thumbnailProcessing(GlobalInfo.CP_Niio, allData);
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING, allData);
            campTown.thumbnailProcessing(GlobalInfo.CP_CampTown, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_AirMT)) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_TongOutdoor)) {
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_Gogo337)) {
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_SBCLUB)) {
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_OKMALL)) {
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_FIRST)) {
            first.thumbnailProcessing(GlobalInfo.CP_FIRST, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_DICAMPING)) {
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_Camping365)) {
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_Totooutdoor)) {
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_WeekEnders)) {
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_Starus)) {
            starus.thumbnailProcessing(GlobalInfo.CP_Starus, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_LeisureMan)) {
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGON)) {
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_Niio)) {
            niio.thumbnailProcessing(GlobalInfo.CP_Niio, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_CCAMPING)) {
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING, allData);
        }
        else if (cpName.equals(GlobalInfo.CP_CampTown)) {
            campTown.thumbnailProcessing(GlobalInfo.CP_CampTown, allData);
        }
        else {
            logger.error(" Unknown cp_name !!");
        }

        logger.info(" Processing is normally end!!");
    }
}
