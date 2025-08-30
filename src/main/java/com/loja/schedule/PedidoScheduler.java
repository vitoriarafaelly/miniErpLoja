package com.loja.schedule;

import com.loja.model.Pedido;
import com.loja.model.domain.StatusPedido;
import com.loja.service.PedidoService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
public class PedidoScheduler {

    private final PedidoService pedidoService;

    public PedidoScheduler(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void marcarPedidosAtrasados() {
        log.info("Schedule: Marcar pedidos atrasados...");
        LocalDateTime limite = LocalDateTime.now().minusHours(48);

        List<Pedido> pedidosAtrasados = pedidoService.buscarPedidosAtrasados(
                StatusPedido.CREATED, limite);

        if (pedidosAtrasados.isEmpty()) {
            return;
        }

        for (Pedido pedido : pedidosAtrasados) {
            pedido.setStatus(StatusPedido.LATE);
        }

        pedidoService.salvarTodos(pedidosAtrasados);
    }
}
