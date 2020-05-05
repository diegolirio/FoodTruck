package com.example.cliente;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) throws InterruptedException {
        //TimeUnit.SECONDS.sleep(30L);
        return new Cliente(id, UUID.randomUUID().toString());
    }

}
