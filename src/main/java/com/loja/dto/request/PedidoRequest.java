package com.loja.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {
    private Long clienteId;
    private List<ItemPedidoRequest> itens;
}
