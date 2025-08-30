package com.loja.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PedidoResponse {
    private Long id;
    private Long clienteId;
    private List<ItemResponse> itens;
    private BigDecimal subtotal;
    private BigDecimal descontos;
    private BigDecimal total;
    private String status;
}
