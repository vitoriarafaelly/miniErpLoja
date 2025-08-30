package com.loja.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(length = 255)
    private String logradouro;

    @Column(length = 10)
    private String numero;

    @Column(length = 255)
    private String complemento;

    @Column(length = 255)
    private String bairro;

    @Column(length = 255)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(nullable = false, length = 8)
    private String cep;
}
