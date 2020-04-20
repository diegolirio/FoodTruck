package com.example.produto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoRestController {

    @GetMapping("/{id}")
    public Produto getById(@PathVariable Long id) {
        return new Produto(id, UUID.randomUUID().toString(), 99.99, UUID.randomUUID().getLeastSignificantBits());
    }

}
