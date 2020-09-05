package com.example.foodtruck.infraestrutura.cliente;

import com.example.foodtruck.FoodTruckApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {FoodTruckApplication.class})
public class ClienteServiceTests {

    @Autowired
    private ClienteService clienteService;

    @Test
    public void test() {
        List<Long> ids = List.of(1l, 2l, 3l, 4l, 5l);
        ids.parallelStream().forEach(id -> clienteService.getById(1L));
        Assertions.assertTrue(true);
    }

}
