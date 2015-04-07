package com.erpy.test;

import com.erpy.utils.GlobalUtils;

/**
 * Created by baeonejune on 15. 4. 7..
 */
public class HashValueTest {
    public static void main (String args[]) throws Exception {
        GlobalUtils globalUtils = new GlobalUtils();
        System.out.println(globalUtils.MD5("alsjflajfdlajlfja"));
        System.out.println(globalUtils.MD5("blsjflajfdlajlfja"));
        System.out.println(globalUtils.MD5("clsjflajfdlajlflajflajflajlfjaldjflajflajflajdlfajlkdsjflajflkajdlfjaksjdfklasdjflajdfijwaoiteuoqwejhroq23ur93joasjhflkashjdfja"));
        System.out.println(globalUtils.MD5(" lsjflajfdlajlfja"));
    }
}
