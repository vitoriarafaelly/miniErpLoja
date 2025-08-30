package com.loja.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemResponse {
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal desconto;
    private BigDecimal subtotal;
}
