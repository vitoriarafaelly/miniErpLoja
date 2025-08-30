package com.loja.repository;

import com.loja.model.Pedido;
import com.loja.model.domain.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Page<Pedido> findAllByStatus(StatusPedido status, Pageable pageable);
}
