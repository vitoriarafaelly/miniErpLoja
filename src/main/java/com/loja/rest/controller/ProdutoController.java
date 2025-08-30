package com.loja.rest.controller;

import com.loja.dto.request.ProdutoRequest;
import com.loja.dto.response.ProdutoResponse;
import com.loja.exception.ProdutoException;
import com.loja.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    @Operation(summary = "Cria um produto")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) throws ProdutoException {
        ProdutoResponse response = produtoService.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos")
    public ResponseEntity<List<ProdutoResponse>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscar(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ProdutoRequest request) throws ProdutoException {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @GetMapping(path = "/listar")
    @Operation(summary = "Lista paginada de produtos")
    public Page<ProdutoResponse> listar(@RequestParam(required = false) Boolean ativo,
                                        @RequestParam(defaultValue = "0", required = false) int page,
                                        @RequestParam(defaultValue = "10", required = false) int size,
                                        @RequestParam(defaultValue = "id", required = false) String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return produtoService.listar(ativo, pageable);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um produto")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws ProdutoException {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
