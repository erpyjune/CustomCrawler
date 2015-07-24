package com.erpy.test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baeonejune on 14. 12. 22..
 */
public class TestDate {
    public static void main(String args[]) throws Exception {
        // Date
        Date date = new Date();
        String strDate = date.toString();
        System.out.println(strDate);

        // SimpleDateFormat
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH-mm-ss");

        for (int i=0; i<100;i++) {
            System.out.println(" SimpleDateFormat:" + sdf.format(date));
            Thread.sleep(1000);
        }

    }
}
