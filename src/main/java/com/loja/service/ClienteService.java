package com.loja.service;

import com.loja.dto.request.ClienteRequest;
import com.loja.dto.response.ClienteResponse;
import com.loja.exception.ClienteException;
import com.loja.model.Cliente;
import com.loja.repository.ClienteRepository;
import com.loja.service.api.viacep.ViaCepClient;
import com.loja.service.api.viacep.dto.ViaCepResponse;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final ViaCepClient viaCepClient;

    private final ModelMapper mapper;

    public ClienteService(ClienteRepository clienteRepository, ViaCepClient viaCepClient, ModelMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.viaCepClient = viaCepClient;
        this.mapper = mapper;
    }

    public ClienteResponse criar(ClienteRequest clienteRequest) throws ClienteException {
        try {
            enriquecerEndereco(clienteRequest);
            Cliente cliente = salvar(mapper.map(clienteRequest, Cliente.class));
            log.info("Cliente criado com sucesso");
            return mapper.map(cliente, ClienteResponse.class);
        }catch (Exception e){
            throw new ClienteException("Erro ao criar cliente", e);
        }

    }

    private void enriquecerEndereco(ClienteRequest clienteRequest) {
        if (clienteRequest.getLogradouro() == null || clienteRequest.getBairro() == null ||
                clienteRequest.getCidade() == null || clienteRequest.getUf() == null) {

            log.info("Realizando chamada para consulta de cep");
            ViaCepResponse viaCep = viaCepClient.buscarCep(clienteRequest.getCep().replaceAll("\\D", ""));
            if (viaCep != null) {
                if (clienteRequest.getLogradouro() == null) clienteRequest.setLogradouro(viaCep.getLogradouro());
                if (clienteRequest.getBairro() == null) clienteRequest.setBairro(viaCep.getBairro());
                if (clienteRequest.getCidade() == null) clienteRequest.setCidade(viaCep.getCidade());
                if (clienteRequest.getUf() == null) clienteRequest.setUf(viaCep.getUf());
            }
        }
    }

    private Cliente buscarClientePorId(Long id) throws ClienteException {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente não encontrado"));//capturar e tratar essas exceções

    }

    private Cliente salvar(Cliente cliente) throws ClienteException {
        try {
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ClienteException("Erro de integridade ao salvar Cliente: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ClienteException("Erro inesperado ao salvar Cliente: " + e.getMessage(), e);
        }
    }

    public ClienteResponse atualizar(Long id, ClienteRequest clienteRequest) throws ClienteException {
        try {
            Cliente cliente = buscarClientePorId(id);

            enriquecerEndereco(clienteRequest);

            mapper.map(cliente, clienteRequest);

            salvar(cliente);

            log.info("Cliente atualizado com sucesso");
            return mapper.map(cliente, ClienteResponse.class);
        }catch (Exception e){
            throw new ClienteException("Erro ao atualizar cliente", e);
        }
    }

    public ClienteResponse buscar(Long id) throws ClienteException {
        Cliente cliente = buscarClientePorId(id);
        return mapper.map(cliente, ClienteResponse.class);
    }

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(cliente ->  mapper.map(cliente, ClienteResponse.class))
                .toList();
    }


    public void deletar(Long id) throws ClienteException {
        Cliente cliente = buscarClientePorId(id);
        try {
            clienteRepository.delete(cliente);
        } catch (DataIntegrityViolationException e) {
            throw new ClienteException("Não é possível excluir cliente.", e);
        } catch (Exception e) {
            throw new ClienteException("Erro inesperado ao excluir cliente: " + e.getMessage(), e);
        }
    }



}
