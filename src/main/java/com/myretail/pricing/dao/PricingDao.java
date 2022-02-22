package com.myretail.pricing.dao;

import com.myretail.pricing.dto.PriceDTO;

/**
 * @author Sunith Soman
 */
public interface PricingDao {

    public PriceDTO getPriceByProductId(Integer productId);
    public PriceDTO updatePriceByProductId(PriceDTO priceDTO);
}

