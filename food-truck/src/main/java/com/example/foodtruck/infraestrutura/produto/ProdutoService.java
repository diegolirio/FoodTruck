package com.example.foodtruck.infraestrutura.produto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "produtoClientService", url = "${services.produto.url}")
public interface ProdutoService {

    @GetMapping("/produtos/{id}")
    Produto getById(@PathVariable Long id);
}
