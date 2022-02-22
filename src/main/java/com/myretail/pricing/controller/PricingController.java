package com.myretail.pricing.controller;

import com.myretail.pricing.exception.PricingException;
import com.myretail.pricing.pojo.Price;
import com.myretail.pricing.services.PricingService;
import com.myretail.pricing.utility.PricingUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sunith Soman
 */
@RestController
@RequestMapping("price")
public class PricingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricingController.class);

    private PricingService pricingService;

    @Autowired
    public PricingController(PricingService pricingService){
        this.pricingService = pricingService;
    }

    /**
     * Service to read the price details.
     * Accepts product id as path parameter and returns Price details.
     * Response codes
     * 200 - Success
     * 404 - If product id is not found
     * 401 - If auth key is invalid
     * 400 - For bad request
     * @param productId
     * @return ResponseEntity<Price>
     */
    @GetMapping(value = "products/{productId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Price> getPrice(@PathVariable("productId") Integer productId){
        ResponseEntity<Price> response = null;
        LOGGER.debug("Product Id:"+productId);
        try {
            Price price = this.pricingService.getProductPrice(productId);
            if (price == null) {
                response = ResponseEntity.notFound().build();
            } else {
                response = ResponseEntity.ok().body(price);
            }
        } catch (PricingException exception) {
            response = PricingUtility.createExceptionResponse(exception);
        }
        return response;
    }

    /**
     * Service to update price.
     * Accepts product id as path parameter and a JSON payload as request body.
     * It will return an updated representation of the resource.
     * Response codes
     * 200 - Success
     * 404 - If product id is not found
     * 401 - If auth key is invalid
     * 400 - For bad request
     * @param productId
     * @param payload
     * @return ResponseEntity<Price>
     */
    @PutMapping(value = "products/{productId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Price> updatePrice(@PathVariable("productId") Integer productId,@RequestBody Price payload){
        LOGGER.debug("Product Id:"+productId);
        LOGGER.debug("Payload:"+payload);
        ResponseEntity<Price> response = null;
        try {
            Price updatedPrice = this.pricingService.updateProductPrice(productId,payload);
            if(updatedPrice==null){
                response = ResponseEntity.notFound().build();
            }else{
                response = ResponseEntity.ok().body(updatedPrice);
            }
        } catch (PricingException exception) {
            response = PricingUtility.createExceptionResponse(exception);
        }
        return response;
    }
}
