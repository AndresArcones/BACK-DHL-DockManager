package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.DTO.ReservaDTO;
import com.andres.Proyecto_Fin_de_Grado.DTO.UsuarioDTO;
import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioReserva;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioUsuario;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuarioImp;
import com.andres.Proyecto_Fin_de_Grado.utilidades.DecodificarJWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.JWT;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MuelleController {
    private final ServicioMuelle servicioMuelle;
    private final RepositorioReserva repositorioReserva;
    private final RepositorioMuelle repositorioMuelle;
    private final ServicioUsuarioImp servicioUsuarioImp;
    private final RepositorioUsuario repositorioUsuario;


    //USER
    @GetMapping("/muelles")
    public Collection<Muelle> mostrarMuelles(){
        return servicioMuelle.mostrarMuelles();
    }

    //USER
    @GetMapping("/muelle/{muelleId}")
    public Muelle muelle(@PathVariable String muelleId){
        return servicioMuelle.muelle(muelleId);
    }

    //add reserva al muelle, al repo de reservas y al usuario
    //USER
    @PostMapping("/reserva/{muelleId}")
    public void aniadirReserva(@PathVariable String muelleId, @RequestBody ReservaDTO reservaDTO, @RequestHeader Map<String, String> headers ){
        JWT token = DecodificarJWT.decode(headers.get("authorization"));
        Usuario userReserva = servicioUsuarioImp.getUsuarioPorNombreUsuario(token.getNombreUsuario());
        Muelle muelle = servicioMuelle.muelle(muelleId);

        Reserva reserva=  new Reserva(muelleId, muelle.getNombre(), reservaDTO.getDni(),reservaDTO.getMatricula(), reservaDTO.getIdPedido(),
                                    reservaDTO.getActividad(), reservaDTO.getFechaHoraReserva(), reservaDTO.getTipoCamion());

        reserva = repositorioReserva.save(reserva);

        muelle.getReservas()[reservaDTO.getTramoHora()] = reserva;
        repositorioMuelle.save(muelle);

        userReserva.getReservas().add(reserva);
        repositorioUsuario.save(userReserva);

    }



}
