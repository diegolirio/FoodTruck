package com.example.foodtruck.infraestrutura.produto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "produtoClientService", url = "http://localhost:8080/produtos")
public interface ProdutoService {

    @GetMapping("/{id}")
    Produto getById(@PathVariable Long id);
}
