package com.example.foodtruck;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PedidoNovo {

    @NotNull
    private Long clienteId;
    @NotNull
    @Size(min = 1)
    private List<Long> itensId;
}
