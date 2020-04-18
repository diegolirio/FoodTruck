package com.example.produto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    private Long id;
    private String nome;
    private Double preco;
    @JsonProperty("categoria_id")
    private Long categoriaId;
}
