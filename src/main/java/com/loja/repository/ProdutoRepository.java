package com.loja.repository;

import com.loja.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByEstoqueLessThan(Integer quantidade);

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
