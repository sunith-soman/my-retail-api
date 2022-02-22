package com.myretail.myretailapi.services;

import com.myretail.myretailapi.exception.PricingException;
import com.myretail.myretailapi.pojo.Price;

/**
 * @author Sunith Soman
 */
public interface PricingService {

    public Price getProductPrice(Integer productId) throws PricingException;
    public Price updateProductPrice(Integer productId, Price payload) throws PricingException;
}
