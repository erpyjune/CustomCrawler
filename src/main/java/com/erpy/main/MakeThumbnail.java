package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.SearchData;
import com.erpy.utils.GlobalUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;

/**
 * Created by baeonejune on 15. 5. 18..
 */
public class MakeThumbnail {

    private static Logger logger = Logger.getLogger("MakeThumbnail");

    public static void main(String[] args) throws Exception {
        int total=0;
        String key;
        String fileName, inPath, outPath;
        String cpName;
        SearchData searchData;
        CrawlIO crawlIO = new CrawlIO();
        StringBuffer sb = new StringBuffer();
        StringBuffer sbOut = new StringBuffer();
        StringBuffer sbIn = new StringBuffer();
        GlobalUtils globalUtils = new GlobalUtils();


        if (args.length == 0) {
            logger.error(" Error argument !!");
            logger.error(" USAGE : cp_name");
            System.exit(-1);
        }

        cpName = args[0];
        logger.info(" cpName : " + cpName);

        String thumbnailDir = "thumbnails";
        String thumbnailLocalPath = "/Users/baeonejune/work/SummaryNode/images";

        // set env.
        String thumbnailFlushTargetDir = sb.
                append(thumbnailLocalPath).
                append("/").
                append(cpName).
                append("/").
                append(thumbnailDir).toString();

        // dir check & if empty is make dir.
        crawlIO.saveDirCheck(thumbnailFlushTargetDir, "");

        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(cpName);
        for (Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();
//            logger.info(String.format(" cp(%s), big_thumb(%s)", searchData.getCpName(), searchData.getThumbUrlBig()));

            fileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());

            inPath = sbIn.
                    append(thumbnailLocalPath).
                    append("/").
                    append(cpName).
                    append("/").
                    append(fileName).toString();

            outPath = sbOut.
                    append(thumbnailFlushTargetDir).
                    append("/").
                    append(fileName).toString();

            sbIn.setLength(0);
            sbOut.setLength(0);

            ////////////////////////////////////////////////
            // make thumbnail.
//            globalUtils.makeThumbnail(inPath, outPath);

            try {
//                globalUtils.makeThumbnail(inPath, outPath);
                Thumbnails.of(new File(inPath)).size(300, 300).toFile(new File(outPath));
                logger.info(String.format(" cp (%s)", searchData.getCpName()));
                logger.info(String.format(" in (%s)", inPath));
                logger.info(String.format(" out(%s)", outPath));
                logger.info(" ---------------------------------------------------------");
            } catch (Exception e) {
                logger.error(e.getStackTrace().toString());
                logger.error(String.format(" cp (%s)", searchData.getCpName()));
                logger.error(String.format(" in (%s)", inPath));
                logger.error(String.format(" out(%s)", outPath));
                logger.error(" ---------------------------------------------------------");
            }

            total++;
        }

        logger.info(String.format(" End normally. Total(%d)", total));
    }
}
