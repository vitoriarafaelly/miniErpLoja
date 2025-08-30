package com.loja.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sku;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "precobruto", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoBruto;

    @Column(nullable = false)
    private Integer estoque = 0;

    @Column(name = "estoqueminimo", nullable = false)
    private Integer estoqueMinimo = 0;

    @Column(nullable = false)
    private Boolean ativo = true;
}
