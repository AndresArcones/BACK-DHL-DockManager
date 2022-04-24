package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.DTO.*;
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
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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

    @PostMapping("/subir-csv-muelle")
    public List<String[]> uploadCSVMuelles(@RequestParam("file") MultipartFile file) {
        List<Muelle> muelles;
        List<String[]> lineas = null;


        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "fichero vacio");
        } else {


            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                lineas = reader.readAll();
                lineas.remove(0);
                if(lineas.size() > 500) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Excede el número maximo de muelles permitido");
                if(lineas.size() == 0) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se encontraron muelles en el fichero");
                List<String[]> datos = Arrays.stream(lineas.toArray(new String[lineas.size()][])).collect(Collectors.toList());

                repositorioMuelle.deleteAll();

                for (String[] muelle: datos) {
                    servicioMuelle.guardarMuelle(new Muelle(muelle[0].toLowerCase(),
                                                muelle[2].toLowerCase(),muelle[1].toLowerCase(),6,8,"libre"));
                }

                    //System.out.println(datos);*/



            } catch (IOException | CsvException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no se ha podido leer el csv");
            }catch(ResponseStatusException ex){
                throw ex;
            }
        }

        return lineas;
    }







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

        // TODO: temporal
        Pedido pedido = new Pedido();
        pedido.setId(reservaDTO.getIdPedido());
        pedido.setMatricula(reservaDTO.getMatricula());
        pedido.setEstado("no entregado");

        servicioPedido.guardarPedido(pedido);


        Instant ahora = SimulateClock.getMomentoSimulacion();
        int hora = muelle.getAperturaMuelle() + reservaDTO.getTramoHora();
        Instant nueva = ahora.atZone(ZoneOffset.UTC)
                .withHour(hora)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toInstant().plus(1, ChronoUnit.DAYS); // TODO: cambiar a +1 para usar el dia siguiente

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

        if(horaDTO.getHora() == -1)
            SimulateClock.setAhora(true);

        else{
            Instant nueva = Instant.ofEpochMilli(horaDTO.getHora()).plusSeconds(2*3600);

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
            servicioMuelle.guardarMuelle(mue);
            repositorioPedido.save(ped);
        }

        if(res!= null)
            throw new ResponseStatusException(HttpStatus.OK,"Barrera abierta");
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No se ha abierto la barrera");
    }

    @GetMapping("/kpi")
    public KpiDTO kpi(){
        double retRes = 0.0;
        double retPed = 0.0;
        double retMue = 0.0;


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

        for(int i=0;i<muelles.size();i++){
            Muelle m = muelles.get(i);
            retMue += m.PorcentajeUso();
        }

        return new KpiDTO(retRes,retPed,retMue/muelles.size());
    }

    @GetMapping("/kpi_muelles")
    public Collection <KpiMuellesDTO> kpi_muelles(){
        List<KpiMuellesDTO> ret = new ArrayList<KpiMuellesDTO>();

        List<Muelle> muelles = repositorioMuelle.findAll();

        for(int i=0;i<muelles.size();i++){
            Muelle m = muelles.get(i);
            ret.add(new KpiMuellesDTO(m.getNombre(),m.PorcentajeUso()));
        }

        return ret;
    }

    @GetMapping("/hora_simulada")
    public HoraSimDTO hora_simulada(){
        HoraSimDTO hora = new HoraSimDTO();
        hora.setHora(SimulateClock.getMomentoSimulacion());
        return hora;
    }

}
