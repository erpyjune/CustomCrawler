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


        if (args.length==0) {
            logger.error(" USAGE: need cp_name");
            System.exit(-1);
        }

        cpName = args[0];

        if (cpName.equals("all") || cpName.equals("ALL")) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT);
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor);
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337);
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB);
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL);
            first.thumbnailProcessing(GlobalInfo.CP_FIRST);
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING);
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365);
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL);
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor);
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders);
            starus.thumbnailProcessing(GlobalInfo.CP_Starus);
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan);
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON);
            niio.thumbnailProcessing(GlobalInfo.CP_Niio);
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING);
        }
        else if (cpName.equals(GlobalInfo.CP_AirMT)) {
            airMT.thumbnailProcessing(GlobalInfo.CP_AirMT);
        }
        else if (cpName.equals(GlobalInfo.CP_TongOutdoor)) {
            tongOutdoor.thumbnailProcessing(GlobalInfo.CP_TongOutdoor);
        }
        else if (cpName.equals(GlobalInfo.CP_Gogo337)) {
            gogo337.thumbnailProcessing(GlobalInfo.CP_Gogo337);
        }
        else if (cpName.equals(GlobalInfo.CP_SBCLUB)) {
            sb.thumbnailProcessing(GlobalInfo.CP_SBCLUB);
        }
        else if (cpName.equals(GlobalInfo.CP_OKMALL)) {
            ok.thumbnailProcessing(GlobalInfo.CP_OKMALL);
        }
        else if (cpName.equals(GlobalInfo.CP_FIRST)) {
            first.thumbnailProcessing(GlobalInfo.CP_FIRST);
        }
        else if (cpName.equals(GlobalInfo.CP_DICAMPING)) {
            diCamping.thumbnailProcessing(GlobalInfo.CP_DICAMPING);
        }
        else if (cpName.equals(GlobalInfo.CP_Camping365)) {
            camping365.thumbnailProcessing(GlobalInfo.CP_Camping365);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGMALL)) {
            campingMall.thumbnailProcessing(GlobalInfo.CP_CAMPINGMALL);
        }
        else if (cpName.equals(GlobalInfo.CP_Totooutdoor)) {
            totoOutdoor.thumbnailProcessing(GlobalInfo.CP_Totooutdoor);
        }
        else if (cpName.equals(GlobalInfo.CP_WeekEnders)) {
            weekEnders.thumbnailProcessing(GlobalInfo.CP_WeekEnders);
        }
        else if (cpName.equals(GlobalInfo.CP_Starus)) {
            starus.thumbnailProcessing(GlobalInfo.CP_Starus);
        }
        else if (cpName.equals(GlobalInfo.CP_LeisureMan)) {
            leisureMan.thumbnailProcessing(GlobalInfo.CP_LeisureMan);
        }
        else if (cpName.equals(GlobalInfo.CP_CAMPINGON)) {
            campingOn.thumbnailProcessing(GlobalInfo.CP_CAMPINGON);
        }
        else if (cpName.equals(GlobalInfo.CP_Niio)) {
            niio.thumbnailProcessing(GlobalInfo.CP_Niio);
        }
        else if (cpName.equals(GlobalInfo.CP_CCAMPING)) {
            cCamping.thumbnailProcessing(GlobalInfo.CP_CCAMPING);
        }
        else {
            logger.error(" Unknown cp_name !!");
        }

        logger.info(" Processing is normally end!!");
    }
}
