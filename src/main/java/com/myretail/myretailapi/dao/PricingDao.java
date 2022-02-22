package com.myretail.myretailapi.dao;

import com.myretail.myretailapi.dto.PriceDTO;

/**
 * @author Sunith Soman
 */
public interface PricingDao {

    public PriceDTO getPriceByProductId(Integer productId);
    public PriceDTO updatePriceByProductId(PriceDTO priceDTO);
}

