package com.erpy.test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baeonejune on 15. 7. 24..
 */
public class DateMillisecTest {
    public static void main(String[] args) throws Exception {


        for (int i=0;i<100;i++) {
            long time = System.currentTimeMillis();
            SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDT = dayTime.format(new Date(time));
            System.out.println(strDT);
        }
    }
}
