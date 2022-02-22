package com.myretail.pricing.services;

import com.myretail.pricing.exception.PricingException;
import com.myretail.pricing.pojo.Price;

/**
 * @author Sunith Soman
 */
public interface PricingService {

    public Price getProductPrice(Integer productId) throws PricingException;
    public Price updateProductPrice(Integer productId, Price payload) throws PricingException;
}
