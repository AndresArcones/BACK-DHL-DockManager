package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioReserva;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReservasController {
    private final ServicioReserva servicioReserva;

    //ADMIN
    @GetMapping("/reservas")
    public Collection<Reserva> mostrarReservas(){
        return servicioReserva.mostrarReservas();
    }

}
