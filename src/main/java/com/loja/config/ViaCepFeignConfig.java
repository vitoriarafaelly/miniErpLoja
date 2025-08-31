package com.loja.config;

import com.loja.exception.CepFormatoInvalidoException;
import com.loja.exception.CepNaoEncontradoException;
import com.loja.exception.ViaCepIndisponivelException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViaCepFeignConfig {
    @Bean
    public feign.codec.ErrorDecoder errorDecoder() {
        return (key, response) -> {
            int status = response.status();
            return switch (status) {
                case 400 -> new CepFormatoInvalidoException("CEP em formato inválido (400)");
                case 404 -> new CepNaoEncontradoException("CEP não encontrado (404).");
                default -> new ViaCepIndisponivelException("Erro ViaCEP: " + status);
            };
        };
    }

    @Bean
    public feign.Retryer retryer() {
        return new feign.Retryer.Default(200, 1000, 3);
    }
}
