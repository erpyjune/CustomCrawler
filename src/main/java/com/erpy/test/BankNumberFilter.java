package com.erpy.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by baeonejune on 15. 6. 12..
 */
public class BankNumberFilter {
    public static void main(String[] args) throws Exception {
        // ‘cat’이라는 패턴 생성
        Pattern p = Pattern.compile("([ 0-9]+[ -]+){1,4}[ 0-9]+");
        // 입력 문자열과 함께 매쳐 클래스 생성
//        Matcher m = p.matcher("one cat, two cats in the yard");
        Matcher m = p.matcher("바나나는 집으로 갑니다 721010-1044991갈까말까 집으로 640203-1033998나는 집으로");
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        // 패턴과 일치하는 문자열을 ‘dog’으로 교체해가며 새로운 문자열을 만든다.
        while (result) {
            m.appendReplacement(sb, "******-*******");
            result = m.find();
        }
        // 나머지 부분을 새로운 문자열 끝에 덫붙인다.
        m.appendTail(sb);
        System.out.println(sb.toString());
    }
}
