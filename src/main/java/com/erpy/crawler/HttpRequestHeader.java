package com.erpy.crawler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baeonejune on 15. 4. 19..
 */
public class HttpRequestHeader {
    HashMap<String, String> httpRequestHeader;
    public HttpRequestHeader(String host, String referer) {
        httpRequestHeader = new HashMap<String, String>();
        httpRequestHeader.put("Host", host);
        httpRequestHeader.put("Referer", referer);
        httpRequestHeader.put("Origin", host);
        httpRequestHeader.put("Accept", "*/*");
        httpRequestHeader.put("Accept-Encoding", "gzip, deflate, sdch");
        httpRequestHeader.put("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
        httpRequestHeader.put("Cache-Control", "max-age=0");
        httpRequestHeader.put("Connection", "keep-alive");
        httpRequestHeader.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
        httpRequestHeader.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpRequestHeader.put("X-Requested-With","XMLHttpRequest");
    }

    public HashMap<String, String> getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public void setHttpRequestHeader(HashMap<String, String> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }
}
