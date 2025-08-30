package com.loja.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoResponse {

    private Long id;

    private String sku;

    private String nome;

    private BigDecimal precoBruto;

    private Integer estoque;

    private Integer estoqueMinimo;

    private Boolean ativo;
}
