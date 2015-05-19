package com.erpy.main;

import com.erpy.parser.AirMT;
import com.erpy.parser.Gogo337;
import com.erpy.parser.SB;
import com.erpy.parser.TongOutdoor;
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

//        logger.info(" Start image download AirMT!!");
//        airMT.thumbnailProcessing("airmt");
//        logger.info(" End   image download AirMT!!");

//        logger.info(" Start image download TongOutdoor!!");
//        tongOutdoor.thumbnailProcessing("tongoutdoor");
//        logger.info(" End   image download TongOutdoor!!");

//        logger.info(" Start image download GoGo337!!");
//        gogo337.thumbnailProcessing("gogo337");
//        logger.info(" End   image download GoGo337!!");

        logger.info(" Start image download sbclub!!");
        sb.thumbnailProcessing("sbclub");
        logger.info(" End   image download sbclub!!");
    }
}
