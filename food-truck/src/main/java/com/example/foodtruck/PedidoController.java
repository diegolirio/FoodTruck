package com.example.foodtruck;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pedidos")
public class PedidoController {

    private final PedidoService service;
    private TaggedCounter perCustomerMessages;

    @Autowired
    public PedidoController(MeterRegistry meterRegistry, final PedidoService service) {
        this.service = service;
        this.perCustomerMessages = new TaggedCounter("pedidos-concluidos-messages", "pedidos", meterRegistry);
    }

    @GetMapping
    public List<Pedido> getAll() {
        return this.service.getAll();
    }

    @PostMapping
    public Pedido novo(@RequestBody @Valid PedidoNovo pedidoNovo) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1L);
        Pedido pedido = this.service.gerarNovo(pedidoNovo);
        perCustomerMessages.increment(pedido.toString());
        return pedido;
    }

}

class TaggedCounter {

    private String name;
    private String tagName;
    private MeterRegistry registry;
    private Map<String, Counter> counters = new HashMap<>();
    public TaggedCounter(String name, String tagName, MeterRegistry registry) {
        this.name = name;
        this.tagName = tagName;
        this.registry = registry;
    }

    public void increment(String tagValue){
        Counter counter = counters.get(tagValue);
        if(counter == null) {
            counter = Counter.builder(name).tags(tagName, tagValue).register(registry);
            counters.put(tagValue, counter);
        }
        counter.increment();
    }
}