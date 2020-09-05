package com.example.foodtruck;

import com.example.foodtruck.infraestrutura.cliente.Cliente;
import com.example.foodtruck.infraestrutura.cliente.ClienteService;
import com.example.foodtruck.infraestrutura.produto.Produto;
import com.example.foodtruck.infraestrutura.produto.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService implements RxCommon {

    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public List<Pedido> getAll() {
        Pedido pedido =
                Pedido.builder()
                        .id("as6d2ffdh")
                        .dataHora(LocalDateTime.now())
                        .cliente(new Cliente(1L, "X-TOP"))
                        .itens(List.of(new Produto(1L, "X-TUDO", 12.99D, 9L)))
                        .build();
        return List.of(pedido);
    }

    public Pedido gerarNovo(PedidoNovo pedidoNovo) {
        Cliente cliente = this.clienteService.getById(pedidoNovo.getClienteId());
        List<Produto> itens = new ArrayList<>();
        pedidoNovo.getItensId().forEach(i -> {
            itens.add(this.produtoService.getById(i));
        });
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .itens(itens)
                .dataHora(LocalDateTime.now())
                .build();
        //pedido = this.pedidoRepository.save(pedido);
        pedido.setId("62sdb325gdfmn");
        return pedido;
    }

    public List<Cliente> threadId(List<Long> ids) {
        List<Observable<Cliente>> clienteObs =
                    ids.stream()
                        .map(c -> getObservableParallel().map(m -> this.clienteService.getById(c)))
                        .collect(Collectors.toList());
        return Observable.from(clienteObs).flatMap(e -> e).toList().toBlocking().single();
//        return ids.stream()
//               .map( id -> clienteService.getById(id) )
//               .collect(Collectors.toList());
    }
}
