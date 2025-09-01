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
import com.loja.service.api.exchangerate.ExchangeService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteService clienteService;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter pedidosCounter;

    @Mock
    private ExchangeService exchangeService;

    private Cliente cliente;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        when(meterRegistry.counter("pedidos_criados_total")).thenReturn(pedidosCounter);
        pedidoService = new PedidoService(pedidoRepository, clienteService, produtoService, modelMapper, meterRegistry, exchangeService);

        cliente = new Cliente();
        cliente.setId(1L);

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Notebook");
        produto.setEstoque(10);
        produto.setPrecoBruto(new BigDecimal("2000"));

        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.CREATED);
    }

    @Test
    void deveCriarPedidoComSucesso() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);

        ItemPedidoRequest itemReq = new ItemPedidoRequest();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(2);
        itemReq.setDesconto(BigDecimal.valueOf(100));
        request.setItens(List.of(itemReq));

        when(clienteService.buscarClientePorId(1L)).thenReturn(cliente);
        when(produtoService.buscarProdutoPorId(1L)).thenReturn(produto);
        when(produtoService.salvar(any(Produto.class))).thenReturn(produto);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponse.class)))
                .thenReturn(new PedidoResponse());

        PedidoResponse response = pedidoService.criar(request);

        assertNotNull(response);
        verify(pedidosCounter).increment();
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void deveLancarErroAoCriarPedidoComEstoqueInsuficiente() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);

        ItemPedidoRequest itemReq = new ItemPedidoRequest();
        itemReq.setProdutoId(1L);
        itemReq.setQuantidade(20);
        itemReq.setDesconto(BigDecimal.ZERO);
        request.setItens(List.of(itemReq));

        when(clienteService.buscarClientePorId(1L)).thenReturn(cliente);
        when(produtoService.buscarProdutoPorId(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class, () -> pedidoService.criar(request));
    }

    @Test
    void deveBuscarPedidoPorId() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        Pedido result = pedidoService.buscarPedidoPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deveLancarErroAoBuscarPedidoInexistente() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pedidoService.buscarPedidoPorId(99L));
    }

    @Test
    void devePagarPedidoComSucesso() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.pagarPedido(1L);

        assertEquals(StatusPedido.PAID, pedido.getStatus());
    }

    @Test
    void deveLancarErroAoPagarPedidoNaoCreated() {
        pedido.setStatus(StatusPedido.CANCELLED);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(PedidoException.class, () -> pedidoService.pagarPedido(1L));
    }

    @Test
    void deveCancelarPedidoComSucesso() {
        ItemPedido item = new ItemPedido();
        item.setProduto(produto);
        item.setQuantidade(2);
        pedido.setItens(List.of(item));

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        pedidoService.cancelarPedido(1L);

        assertEquals(StatusPedido.CANCELLED, pedido.getStatus());
        assertEquals(12, produto.getEstoque());
    }

    @Test
    void deveLancarErroAoCancelarPedidoPago() {
        pedido.setStatus(StatusPedido.PAID);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(PedidoException.class, () -> pedidoService.cancelarPedido(1L));
    }

    @Test
    void deveListarPedidosSemFiltro() {
        Page<Pedido> page = new PageImpl<>(List.of(pedido));
        when(pedidoRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponse.class)))
                .thenReturn(new PedidoResponse());

        Page<PedidoResponse> result = pedidoService.listarPedidos(PageRequest.of(0, 10), null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deveListarPedidosComFiltroDeStatus() {
        Page<Pedido> page = new PageImpl<>(List.of(pedido));
        when(pedidoRepository.findAllByStatus(eq(StatusPedido.CREATED), any(PageRequest.class)))
                .thenReturn(page);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponse.class)))
                .thenReturn(new PedidoResponse());

        Page<PedidoResponse> result = pedidoService.listarPedidos(PageRequest.of(0, 10), StatusPedido.CREATED);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deveSalvarTodosPedidos() {
        pedidoService.salvarTodos(List.of(pedido));
        verify(pedidoRepository, times(1)).saveAll(anyList());
    }
}
