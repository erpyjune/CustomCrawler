package com.erpy.utils;

import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;

import java.util.Iterator;
import java.util.List;
import java.lang.String;

/**
 * Created by baeonejune on 14. 12. 21..
 */
public class GetSeed {
    private static SeedService seedService;

    public static void main (String args[]) {
        String keyword;
        Seed seed;
        seedService = new SeedService();

        List<Seed> seedList = seedService.getAllSeeds();
        Iterator iterator = seedList.iterator();
        while (iterator.hasNext()) {
            seed = (Seed)iterator.next();
            System.out.println(seed.getKeyword());
            System.out.println(seed.getUrl());
            System.out.println("--------------------");
        }
    }
}
