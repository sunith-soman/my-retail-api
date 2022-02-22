package com.myretail.myretailapi.controller;

import com.myretail.myretailapi.exception.PricingException;
import com.myretail.myretailapi.pojo.Price;
import com.myretail.myretailapi.services.PricingService;
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
            response = createExceptionResponse(exception);
        }
        return response;
    }

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
            response = createExceptionResponse(exception);
        }
        return response;
    }

    private ResponseEntity<Price> createExceptionResponse(PricingException exception) {
        if(PricingException.ErrorType.DATA_ERROR.equals(exception.getErrorType())){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.internalServerError().build();
    }
}
