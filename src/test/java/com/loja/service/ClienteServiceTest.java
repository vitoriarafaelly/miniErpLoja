package com.loja.service;

import com.loja.dto.request.ClienteRequest;
import com.loja.dto.response.ClienteResponse;
import com.loja.exception.ClienteException;
import com.loja.model.Cliente;
import com.loja.repository.ClienteRepository;
import com.loja.service.api.viacep.ViaCepClient;
import com.loja.service.api.viacep.dto.ViaCepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ViaCepClient viaCepClient;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequest clienteRequest;
    private Cliente cliente;
    private ViaCepResponse viaCepResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João");
        clienteRequest.setEmail("joao@email.com");
        clienteRequest.setCep("12345678");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");

        viaCepResponse = new ViaCepResponse();
        viaCepResponse.setLogradouro("Rua A");
        viaCepResponse.setBairro("Bairro B");
        viaCepResponse.setCidade("Cidade C");
        viaCepResponse.setUf("SP");
        viaCepResponse.setErro(false);
    }

    @Test
    void deveCriarClienteComSucesso() {
        when(viaCepClient.buscarCep(anyString())).thenReturn(viaCepResponse);
        when(mapper.map(clienteRequest, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteResponse.class)).thenReturn(new ClienteResponse());

        ClienteResponse response = clienteService.criar(clienteRequest);

        assertNotNull(response);
        verify(clienteRepository, times(1)).save(cliente);
        verify(viaCepClient, times(1)).buscarCep("12345678");
    }

    @Test
    void deveLancarExcecaoAoCriarClienteComErro() {
        when(viaCepClient.buscarCep(anyString())).thenReturn(viaCepResponse);

        when(mapper.map(clienteRequest, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(cliente)).thenThrow(new DataIntegrityViolationException("erro"));

        assertThrows(ClienteException.class, () -> clienteService.criar(clienteRequest));
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente result = clienteService.buscarClientePorId(1L);

        assertEquals(cliente.getNome(), result.getNome());
    }

    @Test
    void deveLancarExcecaoAoBuscarClienteInexistente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clienteService.buscarClientePorId(1L));
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(viaCepClient.buscarCep(anyString())).thenReturn(viaCepResponse);
        when(mapper.map(clienteRequest, Cliente.class)).thenReturn(cliente);
        when(mapper.map(cliente, ClienteResponse.class)).thenReturn(new ClienteResponse());
        ClienteResponse response = clienteService.atualizar(1L, clienteRequest);

        assertNotNull(response);
        verify(clienteRepository, times(1)).save(cliente);
        verify(viaCepClient, times(1)).buscarCep(anyString());
    }

    @Test
    void deveListarTodosClientes() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente));
        when(mapper.map(cliente, ClienteResponse.class)).thenReturn(new ClienteResponse());

        List<ClienteResponse> clientes = clienteService.listarTodos();

        assertEquals(1, clientes.size());
    }

    @Test
    void deveListarClientesComPagina() {
        Page<Cliente> page = new PageImpl<>(List.of(cliente));
        when(clienteRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.map(cliente, ClienteResponse.class)).thenReturn(new ClienteResponse());

        Page<ClienteResponse> result = clienteService.listar(null, null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deveDeletarClienteComSucesso() throws ClienteException {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.deletar(1L);

        verify(clienteRepository, times(1)).delete(cliente);
    }

    @Test
    void deveLancarExcecaoAoDeletarClienteComErroIntegridade() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        doThrow(DataIntegrityViolationException.class).when(clienteRepository).delete(cliente);

        assertThrows(ClienteException.class, () -> clienteService.deletar(1L));
    }

}
