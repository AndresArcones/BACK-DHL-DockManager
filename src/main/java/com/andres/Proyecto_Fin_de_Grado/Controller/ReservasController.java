package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioReserva;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioUsuario;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioReserva;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuario;
import com.andres.Proyecto_Fin_de_Grado.utilidades.DecodificarJWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.JWT;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ReservasController {
    private final ServicioReserva servicioReserva;
    private final ServicioMuelle servicioMuelle;
    private final RepositorioReserva repositorioReserva;
    private final RepositorioMuelle repositorioMuelle;
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioUsuario servicioUsuarioImp;

    //ADMIN
    @GetMapping("/reservas")
    public Collection<Reserva> mostrarReservas(){
        return servicioReserva.mostrarReservas();
    }

    @PostMapping("/reserva/anular/{reservaId}")
    public boolean anularReserva(@PathVariable String reservaId, @RequestHeader Map<String, String> headers ){
        Reserva res = repositorioReserva.findById(reservaId).get();
        res.setAnulada(true);
        repositorioReserva.save(res);

        Muelle mue = servicioMuelle.muelle(res.getIdMuelle());
        boolean ret =  mue.anularReserva(res);
        repositorioMuelle.save(mue);
        return ret;

        /* IMPORTANTE DESCOMENTAR AL FINAL PARA CHECKAR USUARIO !!!!!!!!!!!!!????????????
        JWT token = DecodificarJWT.decode(headers.get("authorization"));
        Usuario usu = servicioUsuarioImp.getUsuarioPorNombreUsuario(token.getNombreUsuario());
        usu.anularReserva(res);
        repositorioUsuario.save(usu)
        */
    }

    @GetMapping("/reserva/pantalla")
    public Collection<Reserva> reservasPantalla( ){
        return servicioReserva.reservasPantalla();
    }


    @GetMapping("/mis_reservas")
    public Collection<Reserva>  misReservas(@RequestHeader Map<String, String> headers ) {
        JWT token = DecodificarJWT.decode(headers.get("authorization"));

        Usuario usu = servicioUsuarioImp.getUsuarioPorNombreUsuario(token.getNombreUsuario());
        return usu.getReservas();
    }

}
