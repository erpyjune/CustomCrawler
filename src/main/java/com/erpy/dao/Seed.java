package com.erpy.dao;

public class Seed {
	Integer seedId;
	String  seedKeyword;
	String  crawlUrl;
	String  cpName;
    String cateName1;
    String cateName2;
	String cateName3;

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

    public String getCateName1() {
        return cateName1;
    }

    public void setCateName1(String cateName1) {
        this.cateName1 = cateName1;
    }

    public String getCateName2() {
        return cateName2;
    }

    public void setCateName2(String cateName2) {
        this.cateName2 = cateName2;
    }

	public String getCateName3() {
		return cateName3;
	}

	public void setCateName3(String cateName3) {
		this.cateName3 = cateName3;
	}
}
