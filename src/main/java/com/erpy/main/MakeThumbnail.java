package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.SearchData;
import com.erpy.dao.ThumbnailData;
import com.erpy.dao.ThumbnailDataService;
import com.erpy.utils.GlobalInfo;
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
        int thumbWidth=0, thumbHeight=0;
        SearchData searchData;
        CrawlIO crawlIO = new CrawlIO();
        StringBuffer sb = new StringBuffer();
        StringBuffer sbOut = new StringBuffer();
        StringBuffer sbIn = new StringBuffer();
        GlobalUtils globalUtils = new GlobalUtils();
        GlobalInfo globalInfo = new GlobalInfo();
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnail;
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();


        if (args.length == 0) {
            logger.error(" Error argument !!");
            logger.error(" USAGE : cp_name width height");
            System.exit(-1);
        }


        logger.info(" cpName : " + args[0]);
        logger.info(" width  : " + args[1]);
        logger.info(" height : " + args[2]);

        cpName = args[0];
        thumbWidth = Integer.parseInt(args[1]);
        thumbHeight = Integer.parseInt(args[2]);

        String thumbnailDir = "thumbnails";
        String thumbnailLocalPath = globalInfo.getSaveThumbPath();

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

            // thumb TABLE에서 big_thumb_url을 가져 온다.
            thumbnailData.setProductId(searchData.getProductId());
            thumbnailData.setCpName(searchData.getCpName());
            dbThumbnail = thumbnailDataService.getFindThumbnailData(thumbnailData);
            if (dbThumbnail==null || dbThumbnail.getBigThumbUrl().length()<=0) {
                logger.info(String.format(" Not exist big thumbnail (%s)(%s)", cpName, searchData.getProductId()));
                continue;
            }

            fileName = globalUtils.splieImageFileName(dbThumbnail.getBigThumbUrl());

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
                Thumbnails.of(new File(inPath)).size(thumbWidth, thumbHeight).toFile(new File(outPath));
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
