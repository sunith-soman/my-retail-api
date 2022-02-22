package com.myretail.pricing.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sunith Soman
 */
public class Price {
    @JsonProperty("id")
    private Integer productId;
    @JsonProperty("name")
    private String productName;
    @JsonProperty("current_price")
    private Currency currency;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Price{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", currency=" + currency +
                '}';
    }
}
