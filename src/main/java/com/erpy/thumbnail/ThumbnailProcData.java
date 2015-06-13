package com.erpy.thumbnail;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public class ThumbnailProcData {
    private String savePathPrefix = "/Users/baeonejune/work/SummaryNode/images";
    private String cpName;
    private String prefixHostThumbUrl="http://www.airmt.net";
    private String hostReferer = "http://www.airmt.net";
    private String hostDomain = "www.airmt.net";

    private int htmlCrawlConnectionTimeout=5000;
    private int htmlCrawlReadTimeout=10000;
    private String htmlCrawlEncoding="utf-8";

    private int parserType=1;
    private String parserGroupSelect;
    private String parserSkipPattern;
    private String parserDocumentSelect;
    private String replacePatternFindData;
    private String replacePatternSource;
    private String replacePatternDest;

    private boolean isAllDataCrawl=false;

    public String getSavePathPrefix() {
        return savePathPrefix;
    }

    public void setSavePathPrefix(String savePathPrefix) {
        this.savePathPrefix = savePathPrefix;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public String getPrefixHostThumbUrl() {
        return prefixHostThumbUrl;
    }

    public void setPrefixHostThumbUrl(String prefixHostThumbUrl) {
        this.prefixHostThumbUrl = prefixHostThumbUrl;
    }

    public String getHostReferer() {
        return hostReferer;
    }

    public void setHostReferer(String hostReferer) {
        this.hostReferer = hostReferer;
    }

    public String getHostDomain() {
        return hostDomain;
    }

    public void setHostDomain(String hostDomain) {
        this.hostDomain = hostDomain;
    }

    public int getHtmlCrawlConnectionTimeout() {
        return htmlCrawlConnectionTimeout;
    }

    public void setHtmlCrawlConnectionTimeout(int htmlCrawlConnectionTimeout) {
        this.htmlCrawlConnectionTimeout = htmlCrawlConnectionTimeout;
    }

    public int getHtmlCrawlReadTimeout() {
        return htmlCrawlReadTimeout;
    }

    public void setHtmlCrawlReadTimeout(int htmlCrawlReadTimeout) {
        this.htmlCrawlReadTimeout = htmlCrawlReadTimeout;
    }

    public String getHtmlCrawlEncoding() {
        return htmlCrawlEncoding;
    }

    public void setHtmlCrawlEncoding(String htmlCrawlEncoding) {
        this.htmlCrawlEncoding = htmlCrawlEncoding;
    }

    public int getParserType() {
        return parserType;
    }

    public void setParserType(int parserType) {
        this.parserType = parserType;
    }

    public String getParserGroupSelect() {
        return parserGroupSelect;
    }

    public void setParserGroupSelect(String parserGroupSelect) {
        this.parserGroupSelect = parserGroupSelect;
    }

    public String getParserSkipPattern() {
        return parserSkipPattern;
    }

    public void setParserSkipPattern(String parserSkipPattern) {
        this.parserSkipPattern = parserSkipPattern;
    }

    public String getParserDocumentSelect() {
        return parserDocumentSelect;
    }

    public void setParserDocumentSelect(String parserDocumentSelect) {
        this.parserDocumentSelect = parserDocumentSelect;
    }

    public String getReplacePatternFindData() {
        return replacePatternFindData;
    }

    public void setReplacePatternFindData(String replacePatternFindData) {
        this.replacePatternFindData = replacePatternFindData;
    }

    public String getReplacePatternSource() {
        return replacePatternSource;
    }

    public void setReplacePatternSource(String replacePatternSource) {
        this.replacePatternSource = replacePatternSource;
    }

    public String getReplacePatternDest() {
        return replacePatternDest;
    }

    public void setReplacePatternDest(String replacePatternDest) {
        this.replacePatternDest = replacePatternDest;
    }

    public boolean isAllDataCrawl() {
        return isAllDataCrawl;
    }

    public void setIsAllDataCrawl(boolean isAllDataCrawl) {
        this.isAllDataCrawl = isAllDataCrawl;
    }
}
