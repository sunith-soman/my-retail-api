package com.myretail.myretailapi.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sunith Soman
 */
public class Currency {
    @JsonProperty("value")
    private Double value;
    @JsonProperty("currency_code")
    private String code;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "value=" + value +
                ", code='" + code + '\'' +
                '}';
    }
}
