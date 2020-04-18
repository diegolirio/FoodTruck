package com.example.foodtruck;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pedidos")
public class PedidoController {

    private final PedidoService service;

    @GetMapping
    public List<Pedido> getAll() {
        return this.service.getAll();
    }

    @PostMapping
    public Pedido novo(@RequestBody @Valid PedidoNovo pedidoNovo) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1L);
        return this.service.gerarNovo(pedidoNovo);
    }

}
