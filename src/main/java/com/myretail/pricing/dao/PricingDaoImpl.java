package com.myretail.pricing.dao;

import com.myretail.pricing.dto.PriceDTO;
import com.myretail.pricing.entity.ProductPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author Sunith Soman
 */
@Component
public class PricingDaoImpl implements PricingDao{

    private CassandraTemplate cassandraTemplate;

    @Autowired
    public PricingDaoImpl(CassandraTemplate cassandraTemplate){
        this.cassandraTemplate = cassandraTemplate;
    }

    @Override
    public PriceDTO getPriceByProductId(final Integer productId){
        PriceDTO priceDTO = null;
        ProductPrice priceData = cassandraTemplate.selectOne(Query.query(Criteria.where("product_id").is(productId)), ProductPrice.class);
        if(priceData!=null) {
            priceDTO = getPriceDTO(priceData);
        }
        return priceDTO;
    }

    @Override
    public PriceDTO updatePriceByProductId(PriceDTO priceDTO) {
        ProductPrice priceDataExisting = cassandraTemplate.selectOne(Query.query(Criteria.where("product_id").is(priceDTO.getProductId())), ProductPrice.class);
        PriceDTO priceDTONew = null;
        if(priceDataExisting!=null) {
            priceDataExisting.setPrice(priceDTO.getPrice());
            priceDataExisting.setCurrencyCode(priceDTO.getCurrencyCode());
            priceDataExisting.setLastUpdateTs(Timestamp.from(Instant.now()));
            ProductPrice priceDataUpdated = cassandraTemplate.update(priceDataExisting);
            priceDTONew = getPriceDTO(priceDataUpdated);
        }
        return priceDTONew;
    }

    private PriceDTO getPriceDTO(ProductPrice priceDataUpdated) {
        PriceDTO priceDto = new PriceDTO();
        priceDto.setProductId(priceDataUpdated.getProductId());
        priceDto.setPrice(priceDataUpdated.getPrice());
        priceDto.setCurrencyCode(priceDataUpdated.getCurrencyCode());
        return priceDto;
    }
}
