package com.myretail.pricing.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.myretail.pricing.dto.PriceDTO;
import com.myretail.pricing.dto.UserDTO;
import com.myretail.pricing.entity.ProductPrice;
import com.myretail.pricing.entity.User;
import com.myretail.pricing.exception.PricingException;
import com.myretail.pricing.pojo.Currency;
import com.myretail.pricing.pojo.Price;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.myretail.pricing.constants.PricingConstants.*;

public class PricingUtility {
    private PricingUtility(){};

    public static Price getPrice(String productName, PriceDTO priceDTO) {
        Price price = null;
        if(null!=priceDTO) {
            Currency currency = new Currency();
            currency.setValue(priceDTO.getPrice());
            currency.setCode(priceDTO.getCurrencyCode());

            price = new Price();
            price.setProductId(priceDTO.getProductId());
            price.setProductName(productName);
            price.setCurrency(currency);
        }
        return price;
    }

    public static String getProductName(JsonNode jsonResponseNode){
        String productName = NOT_AVAILABLE;
        if(isValidProductJson(jsonResponseNode)){
            productName = jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).get(PRODUCT_DESC).get(TITLE).asText();
        }
        return productName;
    }

    public static boolean isValidProductJson(JsonNode jsonResponseNode){
        return null!=jsonResponseNode
                && jsonResponseNode.has(DATA) && jsonResponseNode.get(DATA).has(PRODUCT)
                && jsonResponseNode.get(DATA).get(PRODUCT).has(ITEM)
                && jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).has(PRODUCT_DESC)
                && jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).get(PRODUCT_DESC).has(TITLE);
    }

    public static boolean isValidPayload(Price payload) {
        //Check for price & currency code
        if(payload.getCurrency()!=null
                && payload.getCurrency().getCode()!=null
                && payload.getCurrency().getValue()!=null
                && payload.getCurrency().getValue()>0){
            BigDecimal price = BigDecimal.valueOf(payload.getCurrency().getValue());
            if(price.scale()>2){
                payload.getCurrency().setValue(price.setScale(2, RoundingMode.HALF_UP).doubleValue());
            }
            return true;
        }
        return false;
    }

    public static PriceDTO getPriceDTO(ProductPrice priceDataUpdated) {
        PriceDTO priceDto = new PriceDTO();
        priceDto.setProductId(priceDataUpdated.getProductId());
        priceDto.setPrice(priceDataUpdated.getPrice());
        priceDto.setCurrencyCode(priceDataUpdated.getCurrencyCode());
        return priceDto;
    }

    public static UserDTO getUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    public static ResponseEntity<Price> createExceptionResponse(PricingException exception) {
        if(PricingException.ErrorType.DATA_ERROR.equals(exception.getErrorType())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.internalServerError().build();
    }
}
