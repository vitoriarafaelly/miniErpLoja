package com.loja.schedule;

import com.loja.model.Produto;
import com.loja.service.ProdutoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class ReabastecimentoScheduler {

    private final ProdutoService produtoService;

    public ReabastecimentoScheduler(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void verificarEstoqueMinimo() {
        log.info("Schedule: verificando estoque minimo...");
        List<Produto> produtosCriticos = produtoService.buscarProdutosCriticos();

        if (produtosCriticos.isEmpty()) {
            log.info("Nenhum produto abaixo do estoque mínimo hoje.");
            return;
        }

        log.info("Produtos abaixo do estoque mínimo:");
        produtosCriticos.forEach(p -> log.info(
                "Id: " + p.getId() + ", Nome: " + p.getNome() + ", Estoque: " + p.getEstoque()
        ));
    }
}
