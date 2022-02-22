package com.myretail.pricing.services;

import com.myretail.pricing.pojo.Currency;
import com.myretail.pricing.pojo.Price;
import com.myretail.pricing.utility.PricingUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PricingUtilityTests {

    @Test
    public void isValidPayload_returns_true(){
        Price payload = new Price();
        Currency currency = new Currency();
        currency.setValue(33.44);
        currency.setCode("USD");
        payload.setCurrency(currency);
        Assertions.assertTrue(PricingUtility.isValidPayload(payload));
    }

    @Test
    public void isValidPayload_returns_false(){
        Price payload = new Price();
        Currency currency = new Currency();
        currency.setValue(0.0);
        currency.setCode("USD");
        payload.setCurrency(currency);
        Assertions.assertFalse(PricingUtility.isValidPayload(payload));
    }
}
