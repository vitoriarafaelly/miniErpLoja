package com.loja.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class ClienteResponse {

    @NonNull
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String email;

    @NotBlank
    private String cpf;

    private String logradouro;

    private String complemento;

    private String bairro;

    private String numero;

    private String cidade;

    private String uf;

    @NotBlank
    private String cep;
}
