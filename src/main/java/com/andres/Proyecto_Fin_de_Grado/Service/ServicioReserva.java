package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioPedido;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioReserva;
import com.andres.Proyecto_Fin_de_Grado.utilidades.SimulateClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioReserva {
    private final RepositorioReserva repositorioReserva;
    private final RepositorioPedido repositorioPedido;


    //ADMIN
    public Collection<Reserva> mostrarReservas(){
        return repositorioReserva.findAll();
    }

    public Reserva comprobarReserva(String matricula, Instant fechaHoraReserva) {
        List<Reserva> res = repositorioReserva.findByMatriculaEquals(matricula);
        res.removeIf(r -> r.getFechaHoraReserva()!= null && !(r.getFechaHoraReserva().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));
        // si es a las 23:50 técnicamente no es el mismo día, lo ponemos??

        if(res.isEmpty())
            return null;

        else{
            for (Reserva r : res) {
                Instant fh = r.getFechaHoraReserva();
                long d = Duration.between(fh, fechaHoraReserva).toMinutes();
                Pedido pedido = repositorioPedido.findById(r.getIdPedido()).get();

                if(d > 0 && pedido.getHoraEntrada() == null){
                    pedido.setRetraso(d);
                    repositorioPedido.save(pedido);
                }

                if((d >= -10 && d <= 10) || (pedido.getHoraEntrada() != null && pedido.getHoraSalida() == null))
                    return r;
            }
        }

        return null;
    }

    public Collection<Reserva> reservasPantalla(){
        List<Reserva> res = repositorioReserva.findByAnuladaEquals(false);
        res.removeIf(r -> r.getFechaHoraReserva()!= null && !(r.getFechaHoraReserva().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));

        Reserva foo = new Reserva();
        foo.setFechaHoraReserva(SimulateClock.getMomentoSimulacion().minusSeconds(60*11));
        res.add(foo);
        Collections.sort(res);
        List<Reserva> ret = new ArrayList<>();
        int idx = res.indexOf(foo);

        //meter margen de 10 mins antes????
        for(int i=1;i<=4;i++)
            if(i+idx <res.size())
                ret.add(res.get(idx+i));

        return ret;
    }

}
