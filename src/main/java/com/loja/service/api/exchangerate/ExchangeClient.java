package com.loja.service.api.exchangerate;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "exchangeRateClient", url = "https://api.exchangerate.host")
public interface ExchangeClient {

    @GetMapping("/convert")
    Map<String, Object> convert(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "access_key", required = false) String accessKey
    );
}
