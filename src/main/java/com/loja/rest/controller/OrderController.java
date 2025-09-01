package com.loja.rest.controller;

import com.loja.service.PedidoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = " Cotação BRL→USD")
public class OrderController {

    private final PedidoService pedidoService;

    public OrderController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/{id}/usd-total")
    public ResponseEntity<BigDecimal> getTotalUsd(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getOrderTotalInUsd(id));
    }

}
