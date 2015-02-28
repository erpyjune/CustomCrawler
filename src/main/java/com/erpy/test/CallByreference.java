package com.erpy.test;

import com.erpy.dao.CrawlData;
import com.erpy.dao.SearchData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 15. 3. 1..
 */
public class CallByreference {
    public static void main(String[] args) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("1","11");
        map.put("2","22");

        CallByreference.addMap(map);

        for(Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("key:" + key);
            System.out.println("value:" + value);
        }
    }

    public static void addMap(HashMap<String, String> map) throws Exception {
        map.put("3","33");
        map.put("7","77");
    }
}
