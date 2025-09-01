package com.loja.service.api.exchangerate;

import com.loja.exception.ExchangeException;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Log4j2
@Service
public class ExchangeService {

    private static final String ACCESS_KEY = "f3d478995f1cdee3449cf5e2823b5075";

    private final ExchangeClient exchangeClient;

    private BigDecimal cachedRate;
    private Instant cacheTime;

    public ExchangeService(ExchangeClient exchangeClient) {
        this.exchangeClient = exchangeClient;
    }

    public BigDecimal getBrlToUsdRate() {

        if (cachedRate != null && cacheTime != null && Duration.between(cacheTime, Instant.now()).toHours() < 1) {
            log.info("Usando cotação BRL → USD do cache: {}", cachedRate);
            return cachedRate;
        }

        log.info("Buscando cotação BRL → USD na API externa via /convert...");

        Map<String, Object> response = exchangeClient.convert("BRL", "USD", BigDecimal.ONE, ACCESS_KEY);

        if (response == null || !response.containsKey("result")) {
            log.error("Não foi possível obter a cotação BRL → USD");
            throw new ExchangeException("Não foi possível obter a cotação BRL→USD");
        }

        BigDecimal rate = new BigDecimal(response.get("result").toString());
        log.info("Cotação BRL → USD obtida: {}", rate);

        cachedRate = rate;
        cacheTime = Instant.now();

        return rate;
    }
}
