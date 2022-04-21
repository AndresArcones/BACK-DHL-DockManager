package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioPedido;
import com.andres.Proyecto_Fin_de_Grado.utilidades.SimulateClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioPedido {
    private final RepositorioPedido repositorioPedido;

    public void guardarPedido(Pedido pedido){repositorioPedido.save(pedido);}
    public List<Pedido> pedidosMes(){
        List<Pedido> pedidos = repositorioPedido.findAll();

        pedidos.removeIf(p -> p.getHoraEntrada()!= null && !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getMonth().equals(SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getMonth())));

        return pedidos;
    }
    public List<Pedido> pedidosRetrasadosMes(){
        List<Pedido> pedidos = repositorioPedido.findByRetrasoGreaterThan(0);

        pedidos.removeIf(p -> !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getMonth().equals(SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getMonth())));

        return pedidos;
    }

}
