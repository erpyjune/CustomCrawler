package com.erpy.crawler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 14. 11. 30..
 */
public class CrawlSite {
    private static Logger logger = Logger.getLogger(CrawlSite.class.getName());

    private String crawlUrl;
    private String crawlData;
    private String crawlEncoding="euc-kr"; // UTF-8, EUC-KR

    // POST data form param.
    Map<String, String> postFormDataParam;
    Map<String, String> requestHeader;

    private int reponseCode;
    private int socketTimeout=10000;
    private int connectionTimeout=3000;
    private String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36";
    private String REFERER = "http://search.google.com";
    private String CONNECTION = "keep-alive";
    private String ACCEPT = "text/html, */*; q=0.01";
    private String ACCEPT_LANG = "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4";
    private String Content_Type = "application/x-www-form-urlencoded; charset=UTF-8";

    // set method.
    public void setCrawlUrl(String url) {
        this.crawlUrl = url;
    }

    public String getCrawlUrl() {
        return crawlUrl;
    }

    public void setCrawlEncode(String type) {
        this.crawlEncoding = type;
    }
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    public String getCrawlData() {
        return this.crawlData;
    }
    public void setCrawlData(String crawlData) {
        this.crawlData = crawlData;
    }
    public int getReponseCode() {
        return reponseCode;
    }

    public void addPostRequestParam(String name, String value) {
        postFormDataParam.put(name, value);
    }
    public void clearPostRequestParam() {
        postFormDataParam.clear();
    }
    public Map<String, String> getPostRequestParam() {
        return postFormDataParam;
    }
    public void setPostFormDataParam(Map<String, String> map) { this.postFormDataParam = map; }
    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }
    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    // crawling method...
    public String HttpCrawlGetMethod1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(this.crawlUrl);
        HttpEntity entity = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line=null;

        for(Map.Entry<String, String> entry : requestHeader.entrySet()) {
//            logger.info(String.format(" Set request header %s::%s", entry.getKey().trim(), entry.getValue().trim()));
            httpGet.setHeader(entry.getKey().trim(), entry.getValue().trim());
        }

        CloseableHttpResponse response = httpClient.execute(httpGet);
        reponseCode = response.getStatusLine().getStatusCode();

        logger.info(" Response Code : " + response.getStatusLine());

        try {
            entity = response.getEntity();
            if (entity != null) {
                br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }
        } finally {
            if(br!=null) br.close();
            response.close();
        }

        EntityUtils.consume(entity);

        if (sb.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = sb.toString();
        }

        return this.crawlData;
    }

    public String HttpCrawlGetMethod2() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(this.crawlUrl);

        for(Map.Entry<String, String> entry : requestHeader.entrySet()) {
//            logger.info(String.format(" Set request header %s::%s", entry.getKey().trim(), entry.getValue().trim()));
            request.setHeader(entry.getKey().trim(), entry.getValue().trim());
        }

        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        reponseCode = response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        rd.close();

        return this.crawlData;
    }

    public int HttpCrawlGetDataTimeout() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build();

        HttpGet httpGet = new HttpGet(this.crawlUrl);

        for(Map.Entry<String, String> entry : requestHeader.entrySet()) {
//            logger.info(String.format(" Set request header %s::%s", entry.getKey().trim(), entry.getValue().trim()));
            httpGet.setHeader(entry.getKey().trim(), entry.getValue().trim());
        }

        httpGet.setConfig(requestConfig);
        CloseableHttpResponse closeableHttpResponse = httpClient.execute(httpGet);
        reponseCode = closeableHttpResponse.getStatusLine().getStatusCode();

        try {
            //HttpEntity httpEntity = closeableHttpResponse.getEntity();
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(closeableHttpResponse.getEntity().getContent(), this.crawlEncoding));

            String line;
            StringBuffer result = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }

            if (result.length() <= 0) {
                this.crawlData = "";
            } else {
                this.crawlData = result.toString();
            }
        } finally {
            closeableHttpResponse.close();
        }

        return closeableHttpResponse.getStatusLine().getStatusCode();
    }


    ///////////////////////////////////////////////////////////////////////////
    ///
    ///////////////////////////////////////////////////////////////////////////
    public int HttpCrawlPostMethod() throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(crawlUrl);

        for(Map.Entry<String, String> entry : requestHeader.entrySet()) {
//            logger.info(String.format(" Set request header %s::%s", entry.getKey().trim(), entry.getValue().trim()));
            httpPost.setHeader(entry.getKey().trim(), entry.getValue().trim());
        }

        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("mode", "categorymain"));
        urlParameters.add(new BasicNameValuePair("categoryid", "9420101"));
        urlParameters.add(new BasicNameValuePair("startnum", "41"));
        urlParameters.add(new BasicNameValuePair("endnum", "80"));

        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        urlParameters.size();

        HttpResponse response = client.execute(httpPost);

        reponseCode =response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        rd.close();

        return reponseCode;
    }


    ///////////////////////////////////////////////////////////////////////////
    /// Http request post
    ///////////////////////////////////////////////////////////////////////////
    public void HttpPostGetData() throws Exception {
        String jsonQueryString = "{\n" +
                "\t\"query\" : {\n" +
                "    \t\"multi_match\": {\n" +
                "        \t\"query\":                \"nike\",\n" +
                "        \t\"type\":                 \"best_fields\", \n" +
                "        \t\"fields\":               [ \"product_name^2\", \"brand_name^1\", \"keyword^1\" ]\n" +
                "    \t}\n" +
                "    }\n" +
                "}";
        HttpPost httpPost = new HttpPost(crawlUrl);
        httpPost.addHeader("User-Agent", USER_AGENT);
        httpPost.addHeader("Referer", REFERER);
        httpPost.setConfig(RequestConfig.custom().
                setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build());

//        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//        urlParameters.add(new BasicNameValuePair("mode", "categorymain"));
//        urlParameters.add(new BasicNameValuePair("categoryid", "94201"));
//        urlParameters.add(new BasicNameValuePair("startnum", "41"));
//        urlParameters.add(new BasicNameValuePair("endnum", "200"));
//        urlParameters.size();


        logger.info("jsonquery : " + jsonQueryString);
        StringEntity stringEntity = new StringEntity(jsonQueryString);
        httpPost.setEntity(stringEntity);
//        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(httpPost);

        logger.info("repose code : " + response.getStatusLine().getStatusCode());
        reponseCode =response.getStatusLine().getStatusCode();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), this.crawlEncoding));

        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        bufferedReader.close();
    }


    ///////////////////////////////////////////////////////////////////////////
    /// Http request post
    ///////////////////////////////////////////////////////////////////////////
    public int HttpXPUT() throws IOException {

        //String data = "{\"title\" : \"good morning\", \"name\" : \"erpy\", \"date\" : \"20141015\", \"id\" : 123}";
        int responseReturnCode;
        URL url = new URL(crawlUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        //httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setConnectTimeout(5000);
        httpConn.setReadTimeout(5000);
        httpConn.setRequestMethod("POST"); // PUT, DELETE, POST, GET

        OutputStreamWriter osw = new OutputStreamWriter(httpConn.getOutputStream());
        osw.write(crawlData);
        osw.flush();
        osw.close();

        //System.out.println("HTTP Response Code : " + httpConn.getResponseCode());
        responseReturnCode = httpConn.getResponseCode();
        httpConn.disconnect();

        return responseReturnCode;
    }


    ///////////////////////////////////////////////////////////////////////////
    /// Http request POST.
    ///////////////////////////////////////////////////////////////////////////
    public int HttpPostGet() throws Exception {
        HttpPost post = new HttpPost(crawlUrl);
        post.setConfig(RequestConfig.custom().
                setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectionTimeout)
                .build());

        logger.info(" Crawling target : " + crawlUrl);

        // add request headers.
        for(Map.Entry<String, String> entry : requestHeader.entrySet()) {
            logger.info(String.format(" Set request Header %s:%s", entry.getKey().trim(), entry.getValue().trim()));
            post.setHeader(entry.getKey().trim(), entry.getValue().trim());
        }

        // set param
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        for(Map.Entry<String, String> entry : postFormDataParam.entrySet()) {
            logger.info(String.format(" Set request Param %s:%s", entry.getKey().trim(), entry.getValue().trim()));
            urlParameters.add(new BasicNameValuePair(entry.getKey().trim(), entry.getValue().trim()));
        }
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);

        reponseCode =response.getStatusLine().getStatusCode();

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent(), crawlEncoding));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        if (result.length() <= 0) {
            this.crawlData = "";
        } else {
            this.crawlData = result.toString();
        }

        return reponseCode;
    }
}
