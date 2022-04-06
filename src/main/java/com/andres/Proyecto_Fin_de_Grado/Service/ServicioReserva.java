package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ServicioReserva {
    private final RepositorioReserva repositorioReserva;


    //ADMIN
    public Collection<Reserva> mostrarReservas(){
        return repositorioReserva.findAll();
    }
    

}
