package com.example.foodtruck.infraestrutura.cliente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clienteClientService", url = "${services.cliente.url}")
public interface ClienteService {

    @GetMapping("/clientes/{id}")
    Cliente getById(@PathVariable("id") Long id);

}
