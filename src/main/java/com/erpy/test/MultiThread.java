package com.erpy.test;

/**
 * Created by baeonejune on 15. 4. 13..
 */
public class MultiThread extends Thread {
    String name;

    public MultiThread(String name) {
        System.out.println(getName() + " Start threads !!");
        this.name = name;
    }

    public void run() {
        for (int i=0;i<50;i++) {
            System.out.println(getName() + " name : " + name);
            try {
                sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
