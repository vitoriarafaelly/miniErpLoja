package com.loja.repository;

import com.loja.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByAtivo(Boolean ativo, Pageable pageable);
    @Query("SELECT p FROM Produto p WHERE p.estoque < p.estoqueMinimo")
    List<Produto> findByEstoqueLessThanEstoqueMinimo();
}
