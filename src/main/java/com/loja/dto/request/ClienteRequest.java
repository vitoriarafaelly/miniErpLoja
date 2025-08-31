package com.loja.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 11)
    private String cpf;

    private String logradouro;

    private String complemento;

    private String bairro;

    private String numero;

    private String cidade;

    @Size(max = 2, message = "UF deve ter 2 caracteres")
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Size(max = 8)
    private String cep;
}
