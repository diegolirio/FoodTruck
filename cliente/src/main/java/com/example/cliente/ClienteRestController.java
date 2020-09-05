package com.example.cliente;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

//    Map<Integer, Cliente> clienteMap = new Map<Integer, Cliente>();
//
//    public ClienteRestController(MeterRegistry registry) {
//        registry.mapSize("population", people);
//    }

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return new Cliente(id, UUID.randomUUID().toString());
    }

}
