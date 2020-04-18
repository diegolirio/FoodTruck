package com.example.foodtruck;

import com.example.foodtruck.infraestrutura.cliente.Cliente;
import com.example.foodtruck.infraestrutura.produto.Produto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    private String id;
    private LocalDateTime dataHora;
    private Cliente cliente;
    private List<Produto> itens;
}
