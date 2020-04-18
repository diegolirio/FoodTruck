package com.example.foodtruck.infraestrutura.cliente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clienteClientService", url = "http://localhost:8081/clientes")
public interface ClienteService {

    @GetMapping("/{id}")
    Cliente getById(@PathVariable("id") Long id);

}
