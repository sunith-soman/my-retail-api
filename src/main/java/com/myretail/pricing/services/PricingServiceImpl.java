package com.myretail.pricing.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.pricing.dao.PricingDao;
import com.myretail.pricing.dto.PriceDTO;
import com.myretail.pricing.exception.PricingException;
import com.myretail.pricing.pojo.Currency;
import com.myretail.pricing.pojo.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.myretail.pricing.constants.PricingConstants.*;

/**
 * @author Sunith Soman
 */
@Service
public class PricingServiceImpl implements PricingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PricingServiceImpl.class);

    @Value("${product.url}")
    private String productUrl;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private PricingDao pricingDao;

    @Autowired
    public PricingServiceImpl(RestTemplate restTemplate,ObjectMapper objectMapper,PricingDao pricingDao){
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.pricingDao = pricingDao;
    }

    @Override
    public Price getProductPrice(final Integer productId) throws PricingException {

        String productName = readProductName(productId);

        PriceDTO priceDTO = null;
        try {
            priceDTO = pricingDao.getPriceByProductId(productId);
        }
        catch(DataAccessException exception){
            throw new PricingException(exception.getMessage(), PricingException.ErrorType.SYSTEM_ERROR, exception);
        }

        Price price = getPrice(productName, priceDTO);;
        return price;
    }

    @Override
    public Price updateProductPrice(final Integer productId, final Price priceRequest) throws PricingException {
        Price priceResponse = null;
        //Validate payload
        if(!isValidPayload(priceRequest)) {
            throw new PricingException("Bad data in request",PricingException.ErrorType.DATA_ERROR,null);
        }
        PriceDTO priceDTOReq = new PriceDTO();
        priceDTOReq.setProductId(productId);
        priceDTOReq.setPrice(priceRequest.getCurrency().getValue());
        priceDTOReq.setCurrencyCode(priceRequest.getCurrency().getCode());
        try {
            PriceDTO priceDTORes = pricingDao.updatePriceByProductId(priceDTOReq);
            priceResponse = getPrice(priceRequest.getProductName(), priceDTORes);
        }
        catch(DataAccessException exception){
            throw new PricingException(exception.getMessage(),PricingException.ErrorType.DATA_ERROR,null);
        }
        return priceResponse;
    }

    private String readProductName(Integer productId) throws PricingException {
        String productResponse = null;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(productUrl,String.class, productId);
            if(response.getStatusCode().is2xxSuccessful()){
                productResponse = response.getBody();
                LOGGER.debug("Response Body from Product API:"+productResponse);
            }
            else{
                LOGGER.error("Error from Product API:"+response.getStatusCode().value());
            }
        }
        catch (HttpStatusCodeException exception){
            LOGGER.error("Error from Product API:",exception);
        }

        String productName = NOT_AVAILABLE;
        if(productResponse!=null) {
            try {
                JsonNode jsonResponseNode = objectMapper.readTree(productResponse);
                productName = getProductName(jsonResponseNode);
            } catch (JsonProcessingException e) {
                LOGGER.error("Error while parsing product response:", e);
            }
        }
        return productName;
    }

    private Price getPrice(String productName, PriceDTO priceDTO) {
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

    private String getProductName(JsonNode jsonResponseNode){
        String productName = NOT_AVAILABLE;
        if(isValidProductJson(jsonResponseNode)){
            productName = jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).get(PRODUCT_DESC).get(TITLE).asText();
        }
        return productName;
    }

    private boolean isValidProductJson(JsonNode jsonResponseNode){
        return null!=jsonResponseNode
                && jsonResponseNode.has(DATA) && jsonResponseNode.get(DATA).has(PRODUCT)
                && jsonResponseNode.get(DATA).get(PRODUCT).has(ITEM)
                && jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).has(PRODUCT_DESC)
                && jsonResponseNode.get(DATA).get(PRODUCT).get(ITEM).get(PRODUCT_DESC).has(TITLE);
    }

    private boolean isValidPayload(Price payload) {
        //Check for price & currency code
        if(payload.getCurrency()!=null
                && payload.getCurrency().getCode()!=null
                && payload.getCurrency().getValue()!=null
                && payload.getCurrency().getValue()>0){
            BigDecimal price = BigDecimal.valueOf(payload.getCurrency().getValue());
            if(price.scale()>2){
                payload.getCurrency().setValue(price.setScale(2,RoundingMode.HALF_UP).doubleValue());
            }
            return true;
        }
        return false;
    }
}