package com.loja.exception;

public class EstoqueInsuficienteException extends RuntimeException{
    public EstoqueInsuficienteException(String nome) {
        super("Estoque insuficiente para o produto: " + nome);
    }
}
