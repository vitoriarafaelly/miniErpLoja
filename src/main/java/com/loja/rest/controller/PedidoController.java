package com.loja.rest.controller;

import com.loja.dto.request.PedidoRequest;
import com.loja.dto.response.PedidoResponse;
import com.loja.model.domain.StatusPedido;
import com.loja.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @Operation(summary = "Cria um pedido")
    public ResponseEntity<PedidoResponse> criar(@RequestBody PedidoRequest request) {
      PedidoResponse response = pedidoService.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{id}/pagar")
    @Operation(summary = "Paga um pedido")
    public ResponseEntity<Void> pagar(@PathVariable Long id) {
        pedidoService.pagarPedido(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancela um pedido")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Lista paginada de pedidos")
    public Page<PedidoResponse> listar(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(defaultValue = "dataHoraCriacao", required = false) String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return pedidoService.listarPedidos(pageable, status);
    }
}
