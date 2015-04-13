package com.erpy.test;

/**
 * Created by baeonejune on 15. 3. 29..
 */
public class ThreadTest {
    public static void main(String[] args) throws Exception {
        MultiThread mt1 = new MultiThread("erpy1");
        MultiThread mt2 = new MultiThread("erpy2");
        MultiThread mt3 = new MultiThread("erpy3");

        mt1.start();
        mt2.start();
        mt3.start();

        System.out.println(" main end !!");
    }
}
