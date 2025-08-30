package com.loja.service;

import com.loja.dto.request.ProdutoRequest;
import com.loja.dto.response.ProdutoResponse;
import com.loja.exception.ProdutoException;
import com.loja.model.Produto;
import com.loja.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    private final ModelMapper modelMapper;

    public ProdutoService(ProdutoRepository produtoRepository, ModelMapper modelMapper) {
        this.produtoRepository = produtoRepository;
        this.modelMapper = modelMapper;
    }

    public ProdutoResponse criar(ProdutoRequest request) throws ProdutoException {
        Produto produto = salvar(modelMapper.map(request, Produto.class));
        log.info("Produto criado com sucesso");
        return modelMapper.map(produto, ProdutoResponse.class);

    }

    public Produto salvar(Produto produto) throws ProdutoException {
        try {
            log.info("Salvando produto...");
            return produtoRepository.save(produto);
        } catch (DataIntegrityViolationException e) {
            throw new ProdutoException("Erro de integridade ao salvar Produto: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ProdutoException("Erro inesperado ao salvar Produto: " + e.getMessage(), e);
        }
    }

    public List<ProdutoResponse> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produto ->  modelMapper.map(produto, ProdutoResponse.class))
                .toList();
    }

    public Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

    }

    public ProdutoResponse buscar(Long id) {
        Produto produto = buscarProdutoPorId(id);
        return modelMapper.map(produto, ProdutoResponse.class);
    }

    public ProdutoResponse atualizar(Long id, @Valid ProdutoRequest request) throws ProdutoException {
        Produto produto = buscarProdutoPorId(id);

        modelMapper.map(request, produto);

        Produto produtoAtualizado = salvar(produto);
        return modelMapper.map(produtoAtualizado, ProdutoResponse.class);
    }

    public void deletar(Long id) throws ProdutoException {
        Produto produto = buscarProdutoPorId(id);
        try {
            produtoRepository.delete(produto);
        } catch (DataIntegrityViolationException e) {
            throw new ProdutoException("Não é possível excluir este produto.");
        }catch (Exception e) {
            throw new ProdutoException("Erro inesperado ao deletar este produto: " + e.getMessage(), e);
        }
    }

    public Page<ProdutoResponse> listar(Boolean ativo, Pageable pageable) {
        Page<Produto> page;
        if (ativo != null) {
            page = produtoRepository.findByAtivo(ativo, pageable);
        } else {
            page = produtoRepository.findAll(pageable);
        }
        return page.map(p -> modelMapper.map(p, ProdutoResponse.class));
    }
}
