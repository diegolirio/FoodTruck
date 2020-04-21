package com.example.produto;

import com.example.logtracing.log.LogDownstream;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Controller
@LogDownstream
@RequestMapping("/produtos")
public class ProdutoRestController {

    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<Produto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                new Produto(id, UUID.randomUUID().toString(),
                        99.99,
                        UUID.randomUUID().getLeastSignificantBits()));
    }

}
