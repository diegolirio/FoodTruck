package com.example.cliente;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        return new Cliente(id, UUID.randomUUID().toString());
    }

}
