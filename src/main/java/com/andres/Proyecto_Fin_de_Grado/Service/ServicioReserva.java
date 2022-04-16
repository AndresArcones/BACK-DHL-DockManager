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

        if(res.isEmpty())
            return null;

        else{
            for (Reserva r : res) {
                Instant fh = r.getFechaHoraReserva();
                long d = Duration.between(fechaHoraReserva, fh).toMinutes();
                Pedido pedido = repositorioPedido.findById(r.getIdPedido()).get();

                if((d >= -10 && d <= 10) || (pedido.getHoraEntrada() != null && pedido.getHoraSalida() == null))
                    return r;
            }
        }

        return null;
    }

    public Collection<Reserva> reservasPantalla(){
        List<Reserva> res = repositorioReserva.findAll();

        if(res.size()<7) {
            return res;
        }

        Reserva foo = new Reserva();
        foo.setFechaHoraReserva(SimulateClock.getMomentoSimulacion());
        res.add(foo);
        Collections.sort(res);
        List<Reserva> ret = new ArrayList<Reserva>();
        int idx = res.indexOf(foo);

        for(int i=-3;i<4;i++)
            if(i!=0 && i+idx >= 0 && i+idx <res.size())
                ret.add(res.get(idx+i));

        return ret;
    }

}
