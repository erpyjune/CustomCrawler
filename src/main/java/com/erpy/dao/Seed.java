package com.erpy.dao;

public class Seed {
	Integer seedId;
	String  seedKeyword;
	String  crawlUrl;
	String  cpName;

	public int getSeedId() {
		return seedId;
	}
	public void setSeedId(Integer seedId) {
		this.seedId = seedId;
	}
	public String getKeyword() {
		return seedKeyword;
	}
	public void setKeyword(String keyword) {
		this.seedKeyword = keyword;
	}
	public String getUrl() {
		return crawlUrl;
	}
	public void setUrl(String url) {
		this.crawlUrl = url;
	}
	public String getCpName() {
		return cpName;
	}
	public void setCpName(String cpName) {
		this.cpName = cpName;
	}
}
