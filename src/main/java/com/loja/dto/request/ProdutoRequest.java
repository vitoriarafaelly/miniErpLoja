package com.loja.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    private String sku;

    private String nome;

    private BigDecimal precoBruto;

    private Integer estoque;

    private Integer estoqueMinimo;

    private Boolean ativo;
}
