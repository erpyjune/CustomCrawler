package com.erpy.dao;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public class ThumbnailData {
    String dataId="";
    String productId="";
    String cpName="";
    String bigThumbUrl="";

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId.trim();
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName.trim();
    }

    public String getBigThumbUrl() {
        return bigThumbUrl;
    }

    public void setBigThumbUrl(String bigThumbUrl) {
        this.bigThumbUrl = bigThumbUrl;
    }
}
