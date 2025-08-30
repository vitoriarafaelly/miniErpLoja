package com.loja.rest.controller;

import com.loja.dto.request.ClienteRequest;
import com.loja.dto.response.ClienteResponse;
import com.loja.exception.ClienteException;
import com.loja.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@Tag(name = "Clientes", description = "Gerenciamento de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo cliente")
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest clienteRequest) throws ClienteException {
        ClienteResponse response = clienteService.criar(clienteRequest);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id, @RequestBody ClienteRequest clienteRequest) throws ClienteException {
        return ResponseEntity.ok(clienteService.atualizar(id, clienteRequest));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) throws ClienteException {
        return ResponseEntity.ok(clienteService.buscar(id));
    }

    @GetMapping
    @Operation(summary = "Lista todos os clientes")
    public ResponseEntity<List<ClienteResponse>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cliente")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws ClienteException {
       clienteService.deletar(id);
       return ResponseEntity.noContent().build();
    }

}
