package com.erpy.main;

import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

/**
 * Created by erpy on 15. 6. 21..
 */
public class UpdateSeedCate {
    private static Logger logger = Logger.getLogger(UpdateSeedCate.class.getName());
    public static void main (String args[]) throws Exception {
        String buffer;
        String s;
        String token;
        Integer index;

        if (args.length != 2) {
            logger.error(" (USAGE) seed_file_path cp_name");
            System.exit(0);
        }

        Seed seed = new Seed();
        seed.setCpName(args[1]);
        SeedService seedService = new SeedService();

        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr);
        StringTokenizer stringTokenizer;
        while((buffer=br.readLine())!=null) {
            s = buffer.trim();
            if (s.length() == 0)
                continue;

            if (buffer.startsWith("#"))
                continue;

//            logger.info(" "+buffer);

            index = 0;
            seed.setUrl("");
            seed.setKeyword("");
            seed.setCateName1("");
            seed.setCateName2("");
            stringTokenizer = new StringTokenizer(buffer,"|");
            while(stringTokenizer.hasMoreTokens()) {
                token = stringTokenizer.nextToken().trim();
                if (index == 0) {
                    seed.setCateName1(token.trim());
                }
                else if (index==1) {
                    seed.setCateName2(token.trim());
                }
                else if (index==2) {
                    seed.setCateName3(token.trim());
                }
                else if (index==3) {
                    seed.setKeyword(token.trim());
                }
                else {
                    seed.setUrl(token.trim());
                }
                index++;
            }

            if (seed.getUrl().length()>0) {
                logger.info(" Keyword : " + seed.getKeyword());
                logger.info(" Url     : " + seed.getUrl());
                logger.info(" Cate1   : " + seed.getCateName1());
                logger.info(" Cate2   : " + seed.getCateName2());
                logger.info(" Cate3   : " + seed.getCateName3());
                seedService.insertSeed(seed);
                logger.info(" --------------------------------------------------------------");
            }
        }

        fr.close();
        br.close();

        System.out.println("====== end ======");
    }
}
