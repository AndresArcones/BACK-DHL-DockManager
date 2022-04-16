package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.DTO.HoraDTO;
import com.andres.Proyecto_Fin_de_Grado.DTO.InfoBarreraDTO;
import com.andres.Proyecto_Fin_de_Grado.DTO.ReservaDTO;
import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioPedido;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioReserva;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioUsuario;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioReserva;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuarioImp;
import com.andres.Proyecto_Fin_de_Grado.utilidades.DecodificarJWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.JWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.SimulateClock;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
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
    private final ServicioReserva servicioReserva;
    private final RepositorioPedido repositorioPedido;


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

        /* IMPORTANTE DESCOMENTAR AL FINAL PARA CHECKAR USUARIO !!!!!!!!!!!!!????????????
        JWT token = DecodificarJWT.decode(headers.get("authorization"));
        Usuario userReserva = servicioUsuarioImp.getUsuarioPorNombreUsuario(token.getNombreUsuario());*/
        Muelle muelle = servicioMuelle.muelle(muelleId);

        //CAMBIAR A SUMAR TRAMO A APERTURA
        Instant ahora = LocalDateTime.now().toInstant(ZoneOffset.of("+00:00"));
        String[] hora = reservaDTO.getFechaHoraReserva().split(":");
        Instant nueva = ahora.atZone(ZoneOffset.UTC)
                .withHour(Integer.parseInt(hora[0]))
                .withMinute(Integer.parseInt(hora[1]))
                .withSecond(0)
                .withNano(0)
                .toInstant().plus(0, ChronoUnit.DAYS); //CAMBIAR A +1 AL FINALIZAR CHECKEO

        Reserva reserva=  new Reserva(muelleId, muelle.getNombre(), reservaDTO.getDni(),reservaDTO.getMatricula(), reservaDTO.getIdPedido(),
                                    reservaDTO.getActividad(), nueva, reservaDTO.getTipoCamion());

        reserva = repositorioReserva.save(reserva);

        muelle.getReservas()[reservaDTO.getTramoHora()] = reserva;
        repositorioMuelle.save(muelle);

        /* IMPORTANTE DESCOMENTAR AL FINAL PARA CHECKAR USUARIO !!!!!!!!!!!!!????????????
        userReserva.getReservas().add(reserva);
        repositorioUsuario.save(userReserva);*/

    }

    @PostMapping("/hora")
    public void cambiarHora(@RequestBody HoraDTO horaDTO) {

        if(horaDTO.getHora().equals("ahora"))
            SimulateClock.setAhora(true);

        else{
            Instant ahora = LocalDateTime.now().toInstant(ZoneOffset.of("+00:00"));
            String[] hora = horaDTO.getHora().split(":");
            Instant nueva = ahora.atZone(ZoneOffset.UTC)
                    .withHour(Integer.parseInt(hora[0]))
                    .withMinute(Integer.parseInt(hora[1]))
                    .withSecond(0)
                    .withNano(0)
                    .toInstant();

            SimulateClock.setAhora(false);
            SimulateClock.setMomentoSimulacion(nueva);
        }
        //return SimulateClock.getMomentoSimulacion();
    }

    @PostMapping("/barrera")
    public Reserva accionarBarrera(@RequestBody InfoBarreraDTO infoBarreraDTO){

        Reserva res =  servicioReserva.comprobarReserva(infoBarreraDTO.getMatricula(),SimulateClock.getMomentoSimulacion()) ;

        if(res != null){
            Pedido ped = repositorioPedido.findById(res.getIdPedido()).get();
            Muelle mue = repositorioMuelle.findById(res.getIdMuelle()).get();

            if(ped.getHoraEntrada() == null) {
                ped.setHoraEntrada(SimulateClock.getMomentoSimulacion());
                mue.setEstado("ocupado");

                if(mue.getTipoMuelle() == "carga")
                    ped.setEstado("cargando");
                else
                    ped.setEstado("descargando");
            }
            else {
                ped.setHoraSalida(SimulateClock.getMomentoSimulacion());

                if(mue.getTipoMuelle() == "carga")
                    ped.setEstado("cargado");
                else
                    ped.setEstado("descargado");
                mue.setEstado("libre");
            }

            repositorioPedido.save(ped);
        }

        return res;
    }
}
