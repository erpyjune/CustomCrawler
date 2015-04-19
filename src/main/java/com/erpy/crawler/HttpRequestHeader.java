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
        httpRequestHeader.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpRequestHeader.put("Accept-Encoding", "gzip, deflate, sdch");
        httpRequestHeader.put("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4");
        httpRequestHeader.put("Cache-Control", "max-age=0");
        httpRequestHeader.put("Connection", "keep-alive");
        httpRequestHeader.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4");
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
