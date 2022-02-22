package com.myretail.myretailapi.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.myretailapi.dao.PricingDao;
import com.myretail.myretailapi.dto.PriceDTO;
import com.myretail.myretailapi.exception.PricingException;
import com.myretail.myretailapi.pojo.Currency;
import com.myretail.myretailapi.pojo.Price;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        Future<String> productName = executorService.submit(() -> {
//            return readProductName(productId);
//        });
        String productName = readProductName(productId);

        PriceDTO priceDTO = null;
        try {
            priceDTO = pricingDao.getPriceByProductId(productId);
        }
        catch(Exception exception){
            throw new PricingException(exception.getMessage(), PricingException.ErrorType.SYSTEM_ERROR, exception);
        }

        Price price = getPrice(productName, priceDTO);;
        return price;
    }

    private String readProductName(Integer productId) throws PricingException {
        String productResponse = null;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(productUrl,String.class, productId);
            if(response.getStatusCode().is2xxSuccessful()){
                productResponse = response.getBody();
                LOGGER.info("Response Body from Product API:"+productResponse);
            }
            else{
                LOGGER.error("Error from Product API:"+response.getStatusCode().value());
            }
        }
        catch (HttpStatusCodeException exception){
            LOGGER.error("Error from Product API:",exception);
        }
        catch (Exception exception){
            throw new PricingException(exception.getMessage(),PricingException.ErrorType.SYSTEM_ERROR,exception);
        }

        String productName = "Not Available";
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

    @Override
    public Price updateProductPrice(final Integer productId, final Price priceRequest) throws PricingException {
        Price priceResponse = null;
        //Validate payload
        if(isValidPayload(priceRequest)){
            PriceDTO priceDTOReq = new PriceDTO();
            priceDTOReq.setProductId(productId);
            priceDTOReq.setPrice(priceRequest.getCurrency().getValue());
            priceDTOReq.setCurrencyCode(priceRequest.getCurrency().getCode());
            PriceDTO priceDTORes = pricingDao.updatePriceByProductId(priceDTOReq);
            priceResponse = getPrice(priceRequest.getProductName(),priceDTORes);
        }
        else{
            throw new PricingException("Bad data in request",PricingException.ErrorType.DATA_ERROR,null);
        }
        return priceResponse;
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
        String productName = "Not Available";
        if(isValidProductJson(jsonResponseNode)){
            productName = jsonResponseNode.get("data").get("product").get("item").get("product_description").get("title").asText();
        }
        return productName;
    }

    private boolean isValidProductJson(JsonNode jsonResponseNode){
        return null!=jsonResponseNode
                && jsonResponseNode.has("data") && jsonResponseNode.get("data").has("product")
                && jsonResponseNode.get("data").get("product").has("item")
                && jsonResponseNode.get("data").get("product").get("item").has("product_description")
                && jsonResponseNode.get("data").get("product").get("item").get("product_description").has("title");
    }

    private boolean isValidPayload(Price payload) {
        //Check for price & currency code
        if(payload.getCurrency()!=null
                && payload.getCurrency().getCode()!=null
                && payload.getCurrency().getValue()!=null
                && !Double.valueOf(0).equals(payload.getCurrency().getValue())){
            BigDecimal price = BigDecimal.valueOf(payload.getCurrency().getValue());
            if(price.precision()>4){
                payload.getCurrency().setValue(price.round(new MathContext(4,RoundingMode.HALF_UP)).doubleValue());
            }
            return true;
        }
        return false;
    }
}