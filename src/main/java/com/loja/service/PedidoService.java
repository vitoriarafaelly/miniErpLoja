package com.loja.service;

import com.loja.dto.request.ItemPedidoRequest;
import com.loja.dto.request.PedidoRequest;
import com.loja.dto.response.PedidoResponse;
import com.loja.exception.EstoqueInsuficienteException;
import com.loja.exception.PedidoException;
import com.loja.model.Cliente;
import com.loja.model.ItemPedido;
import com.loja.model.Pedido;
import com.loja.model.Produto;
import com.loja.model.domain.StatusPedido;
import com.loja.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ClienteService clienteService;

    private final ProdutoService produtoService;

    private final ModelMapper modelMapper;

    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService, ProdutoService produtoService, ModelMapper modelMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest request) {
        try {
            Cliente cliente = clienteService.buscarClientePorId(request.getClienteId());

            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setStatus(StatusPedido.CREATED);

            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal totalDesconto = BigDecimal.ZERO;

            List<ItemPedido> itens = new ArrayList<>();
            for(ItemPedidoRequest item : request.getItens()){

                Produto produto = produtoService.buscarProdutoPorId(item.getProdutoId());

                if (produto.getEstoque() < item.getQuantidade()) {
                    throw new EstoqueInsuficienteException(produto.getNome());
                }

                produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                produtoService.salvar(produto);

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPedido(pedido);
                itemPedido.setProduto(produto);
                itemPedido.setQuantidade(item.getQuantidade());
                itemPedido.setDesconto(item.getDesconto() != null ? item.getDesconto() : BigDecimal.ZERO);

                BigDecimal itemSubtotal = produto.getPrecoBruto()
                        .multiply(BigDecimal.valueOf(item.getQuantidade()));
                BigDecimal itemTotal = itemSubtotal.subtract(item.getDesconto());
                itemPedido.setSubtotal(itemTotal);

                subtotal = subtotal.add(itemSubtotal);
                totalDesconto = totalDesconto.add(item.getDesconto());

                itens.add(itemPedido);

            }

            pedido.setItens(itens);
            pedido.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_EVEN));
            pedido.setDescontos(totalDesconto.setScale(2, RoundingMode.HALF_EVEN));
            pedido.setTotal(subtotal.subtract(totalDesconto).setScale(2, RoundingMode.HALF_EVEN));

            Pedido pedidoSalvo = salvar(pedido);
            return modelMapper.map(pedidoSalvo, PedidoResponse.class);
        }catch (Exception e){
            throw new PedidoException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    public Pedido salvar(Pedido pedido) {
        try {
            log.info("Salvando pedido...");
            return pedidoRepository.save(pedido);
        } catch (DataIntegrityViolationException e) {
            throw new PedidoException("Erro de integridade ao salvar Pedido: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new PedidoException("Erro inesperado ao salvar Pedido: " + e.getMessage(), e);
        }
    }

    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

    }

    @Transactional
    public void pagarPedido(Long id) {
        Pedido pedido = buscarPedidoPorId(id);

        if (pedido.getStatus() != StatusPedido.CREATED) {
            throw new PedidoException("Só pedidos CREATED podem ser pagos");
        }

        pedido.setStatus(StatusPedido.PAID);
        salvar(pedido);
    }

    @Transactional
    public void cancelarPedido(Long id) {
        try {
            Pedido pedido = buscarPedidoPorId(id);

            if (pedido.getStatus() == StatusPedido.PAID) {
                throw new PedidoException("Não é possível cancelar pedidos pagos");
            }

            pedido.getItens().forEach(i -> {
                Produto produto = i.getProduto();
                produto.setEstoque(produto.getEstoque() + i.getQuantidade());
                produtoService.salvar(produto);
            });

            pedido.setStatus(StatusPedido.CANCELLED);
            pedidoRepository.save(pedido);
        }catch (Exception e){
            throw new PedidoException("Erro ao cancelar pedido: " + e.getMessage(), e);
        }
    }

    public Page<PedidoResponse> listarPedidos(Pageable pageable, StatusPedido status) {
        Page<Pedido> page;
        if (status != null) {
            page = pedidoRepository.findAllByStatus(status, pageable);
        } else {
            page = pedidoRepository.findAll(pageable);
        }
        return page.map(p -> modelMapper.map(p, PedidoResponse.class));
    }

    public List<Pedido> buscarPedidosAtrasados(StatusPedido statusPedido, LocalDateTime limite) {
        log.info("Buscando pedidos atrasados...");
        return pedidoRepository.findByStatusAndDataHoraCriacaoBefore(statusPedido, limite);
    }

    public void salvarTodos(List<Pedido> pedidos) {
        log.info("Salvando pedidos...");
        pedidoRepository.saveAll(pedidos);
    }
}
