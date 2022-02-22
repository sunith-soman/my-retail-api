package com.myretail.myretailapi.dao;

import com.myretail.myretailapi.dto.PriceDTO;
import com.myretail.myretailapi.entity.PriceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Component;

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
        PriceData priceData = cassandraTemplate.selectOne(Query.query(Criteria.where("product_id").is(productId)), PriceData.class);
        if(priceData!=null) {
            priceDTO = getPriceDTO(priceData);
        }
        return priceDTO;
    }

    @Override
    public PriceDTO updatePriceByProductId(PriceDTO priceDTO) {
        PriceData priceDataExisting = cassandraTemplate.selectOne(Query.query(Criteria.where("product_id").is(priceDTO.getProductId())), PriceData.class);
        PriceDTO priceDTONew = null;
        if(priceDataExisting!=null) {
            priceDataExisting.setPrice(priceDTO.getPrice());
            priceDataExisting.setCurrencyCode(priceDTO.getCurrencyCode());
            PriceData priceDataUpdated = cassandraTemplate.update(priceDataExisting);
            priceDTONew = getPriceDTO(priceDataUpdated);
        }
        return priceDTONew;
    }

    private PriceDTO getPriceDTO(PriceData priceDataUpdated) {
        PriceDTO priceDto = new PriceDTO();
        priceDto.setProductId(priceDataUpdated.getProductId());
        priceDto.setPrice(priceDataUpdated.getPrice());
        priceDto.setCurrencyCode(priceDataUpdated.getCurrencyCode());
        return priceDto;
    }
}
