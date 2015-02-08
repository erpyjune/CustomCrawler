package com.erpy.utils;

import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;
import java.util.StringTokenizer;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class UpdateSeed {
    private static SeedService seedService;

    public static void main (String args[]) throws IOException {
        String buffer;
        String s;
        String token;
        Integer index;

//        if (args.length != 2) {
//            System.out.println("(USAGE) seed_file_path cp_name");
//            System.exit(0);
//        }

        args[0] = "/Users/baeonejune/work/social_shop/data/okoutdoor.dat";
        args[1] = "okmall";

        Seed seed = new Seed();
        seed.setCpName(args[1]);
        seedService = new SeedService();

        System.out.println("seed_file_path : " + args[0]);

        FileReader fr = new FileReader(args[0]);
        BufferedReader br = new BufferedReader(fr);
        StringTokenizer stringTokenizer=null;
        while((buffer=br.readLine())!=null) {
            s = StringUtils.trim(buffer);
            if (s.length() == 0) {
                continue;
            }

            System.out.println(buffer);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

            index = 0;
            stringTokenizer = new StringTokenizer(buffer,"|");
            while(stringTokenizer.hasMoreTokens()) {
                token = StringUtils.trim(stringTokenizer.nextToken());
                System.out.println("token:"+token);
                System.out.println("cp_name:"+args[1]);

                if (index == 0) {
                    seed.setKeyword(token);
                } else {
                    seed.setUrl(token);
                }
                index++;
            }

            seedService.insertSeed(seed);
            System.out.println("---------------------------------");
        }

        fr.close();
        br.close();

        System.out.println("====== end ======");
    }
}
