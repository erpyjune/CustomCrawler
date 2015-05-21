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
        else {
            logger.error(" Unknown cp_name !!");
        }
    }
}
