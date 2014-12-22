package com.erpy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baeonejune on 14. 12. 22..
 */
public class DateInfo {
    private String currDateTime;

    public DateInfo () {
        //Date date = new Date();
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        //this.currDateTime = sdf.format(date);
        //System.out.println("SimpleDateFormat:" + sdf.format(date));
    }

    public String getCurrDateTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        currDateTime = sdf.format(date);
        return currDateTime;
    }
}
