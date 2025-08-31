package com.loja.service;

import com.loja.dto.request.ProdutoRequest;
import com.loja.dto.response.ProdutoResponse;
import com.loja.exception.ProdutoException;
import com.loja.model.Produto;
import com.loja.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProdutoServiceTest {

    private ProdutoRepository produtoRepository;
    private ModelMapper mapper;
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoRequest produtoRequest;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        mapper = new ModelMapper();
        produtoService = new ProdutoService(produtoRepository, mapper);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setPrecoBruto(BigDecimal.valueOf(2000));
        produto.setEstoque(5);
        produto.setAtivo(true);

        produtoRequest = new ProdutoRequest();
        produtoRequest.setNome("Notebook");
        produtoRequest.setPrecoBruto(BigDecimal.valueOf(2000));
        produtoRequest.setEstoque(5);
        produtoRequest.setAtivo(true);
    }

    @Test
    void deveCriarProdutoComSucesso() throws ProdutoException {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoResponse response = produtoService.criar(produtoRequest);

        assertNotNull(response);
        assertEquals("Notebook", response.getNome());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveLancarErroAoSalvarProdutoDuplicado() {
        when(produtoRepository.save(any(Produto.class))).thenThrow(new DataIntegrityViolationException("Duplicado"));

        assertThrows(ProdutoException.class, () -> produtoService.criar(produtoRequest));
    }

    @Test
    void deveListarTodosProdutos() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<ProdutoResponse> produtos = produtoService.listarTodos();

        assertEquals(1, produtos.size());
        assertEquals("Notebook", produtos.get(0).getNome());
    }

    @Test
    void deveBuscarProdutoPorIdComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoResponse response = produtoService.buscar(1L);

        assertNotNull(response);
        assertEquals("Notebook", response.getNome());
    }

    @Test
    void deveLancarErroAoBuscarProdutoInexistente() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produtoService.buscar(99L));
    }

    @Test
    void deveAtualizarProdutoComSucesso() throws ProdutoException {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoRequest updateRequest = new ProdutoRequest();
        updateRequest.setNome("Notebook Gamer");
        updateRequest.setPrecoBruto(BigDecimal.valueOf(3500));
        updateRequest.setEstoque(10);
        updateRequest.setAtivo(true);

        ProdutoResponse response = produtoService.atualizar(1L, updateRequest);

        assertEquals("Notebook Gamer", response.getNome());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    void deveDeletarProdutoComSucesso() throws ProdutoException {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        produtoService.deletar(1L);

        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    void deveLancarErroAoDeletarProdutoComRestricao() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        doThrow(new DataIntegrityViolationException("Restrição")).when(produtoRepository).delete(produto);

        assertThrows(ProdutoException.class, () -> produtoService.deletar(1L));
    }

    @Test
    void deveListarProdutosPaginados() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        Page<Produto> page = new PageImpl<>(List.of(produto));

        when(produtoRepository.findAll(pageable)).thenReturn(page);

        Page<ProdutoResponse> responsePage = produtoService.listar(null, pageable);

        assertEquals(1, responsePage.getTotalElements());
        assertEquals("Notebook", responsePage.getContent().get(0).getNome());
    }

    @Test
    void deveListarProdutosPorAtivo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Produto> page = new PageImpl<>(List.of(produto));

        when(produtoRepository.findByAtivo(true, pageable)).thenReturn(page);

        Page<ProdutoResponse> responsePage = produtoService.listar(true, pageable);

        assertEquals(1, responsePage.getTotalElements());
        assertTrue(responsePage.getContent().get(0).getAtivo());
    }

    @Test
    void deveListarProdutosCriticos() {
        when(produtoRepository.findByEstoqueLessThanEstoqueMinimo()).thenReturn(List.of(produto));

        List<Produto> criticos = produtoService.buscarProdutosCriticos();

        assertEquals(1, criticos.size());
    }
}
