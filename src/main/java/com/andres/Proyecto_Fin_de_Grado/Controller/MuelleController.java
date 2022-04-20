package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.DTO.HoraDTO;
import com.andres.Proyecto_Fin_de_Grado.DTO.InfoBarreraDTO;
import com.andres.Proyecto_Fin_de_Grado.DTO.KpiDTO;
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
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioPedido;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioReserva;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuarioImp;
import com.andres.Proyecto_Fin_de_Grado.utilidades.DecodificarJWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.JWT;
import com.andres.Proyecto_Fin_de_Grado.utilidades.SimulateClock;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private final ServicioPedido servicioPedido;


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

        Instant ahora = SimulateClock.getMomentoSimulacion();
        int hora = muelle.getAperturaMuelle() + reservaDTO.getTramoHora() -1;
        Instant nueva = ahora.atZone(ZoneOffset.UTC)
                .withHour(hora)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant().plus(0, ChronoUnit.DAYS); //CAMBIAR A +1 AL FINALIZAR CHECKEO

        Reserva reserva=  new Reserva(muelleId, muelle.getNombre(), reservaDTO.getDni(),reservaDTO.getMatricula(), reservaDTO.getIdPedido(),
                                    reservaDTO.getActividad(), nueva, reservaDTO.getTipoCamion());

        reserva = repositorioReserva.save(reserva);

        muelle.getReservas()[reservaDTO.getTramoHora()] = reserva.getId();
        repositorioMuelle.save(muelle);

        userReserva.aniadirReserva(reserva.getId());
        repositorioUsuario.save(userReserva);


        throw new ResponseStatusException(HttpStatus.OK,"Reserva realizada correctamente");


    }

    @PostMapping("/hora")
    public void cambiarHora(@RequestBody HoraDTO horaDTO) {

        if(horaDTO.getHora() == 0)
            SimulateClock.setAhora(true);

        else{
            Instant nueva = Instant.ofEpochMilli(horaDTO.getHora());

            SimulateClock.setAhora(false);
            SimulateClock.setMomentoSimulacion(nueva);
        }

        if(SimulateClock.getMomentoSimulacion() != null)
            throw new ResponseStatusException(HttpStatus.OK,"Hora cambiada a " + SimulateClock.getMomentoSimulacion().toString());
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Error al cambiar la hora");
    }

    @PostMapping("/barrera")
    public void accionarBarrera(@RequestBody InfoBarreraDTO infoBarreraDTO){

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

                ped.setTiempoTardado(Duration.between(ped.getHoraEntrada(), ped.getHoraSalida()).toMinutes());
                mue.setEstado("libre");
            }

            repositorioPedido.save(ped);
        }

        if(res!= null)
            throw new ResponseStatusException(HttpStatus.OK,"Barrera abierta");
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No se ha abierto la barrera");
    }

    @GetMapping("/kpi")
    public KpiDTO kpi(){
        String ret = "";
        double retRes = 0.0;
        double retPed = 0.0;
        Map<String, Double> retMue = new LinkedHashMap<String, Double>();


        int numPedidos = servicioPedido.pedidosMes().size();
        int numPedidosRetrasados = servicioPedido.pedidosRetrasadosMes().size();

        int numResAnuladas  = repositorioReserva.findByAnuladaEquals(true).size();
        int numRes = repositorioReserva.findAll().size();

        if(numRes > 0)
            retRes = (double) numResAnuladas*100/numRes;
        else
            retRes = 0;

        if(numPedidos > 0)
            retPed = (double) numPedidosRetrasados*100/numPedidos;
        else
            retPed = 0;

        List<Muelle> muelles = repositorioMuelle.findAll();
        //nombreMuelles = new String[muelles.size()];
        //retMue = new double[muelles.size()];

        for(int i=0;i<muelles.size();i++){
            Muelle m = muelles.get(i);
            retMue.put(m.getNombre(),m.PorcentajeUso());
        }

        return new KpiDTO(retRes,retPed,retMue);
    }

    @GetMapping("/pedidos_hasta_ahora")
    public List<Pedido> pedidos(){
        List<Pedido> ret = repositorioPedido.findByEstadoEquals("cargado");
        ret.addAll(repositorioPedido.findByEstadoEquals("descargado"));
        ret.removeIf(p -> p.getHoraEntrada()!= null && !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));

        return ret;
    }

    @GetMapping("/pedidos_dia")
    public Map<String,List<Pedido>> pedidosDia(){
        List<Pedido> pedidos = repositorioPedido.findByEstadoEquals("cargado");
        pedidos.addAll(repositorioPedido.findByEstadoEquals("descargado"));
        pedidos.removeIf(p -> p.getHoraEntrada()!= null && !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));

        List<Muelle> muelles = repositorioMuelle.findAll();

        Map<String, List<Pedido>> map = new LinkedHashMap<String, List<Pedido>>();

        for(Muelle m : muelles){
            String nombre = m.getNombre();
            List<Pedido> lista = new ArrayList<>();

            for(String id : m.getReservas())
                if(id != null){
                    Reserva r = repositorioReserva.findById(id).get();
                    for(Pedido p : pedidos)
                        if(r.getIdPedido().equals(p.getId()))
                            lista.add(p);
                }


            if(lista.size()>0)
                map.put(nombre,lista);
            else
                map.put(nombre,null);
        }

        return map;
    }
}
