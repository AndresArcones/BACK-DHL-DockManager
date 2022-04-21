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
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PedidoController {
    private final ServicioMuelle servicioMuelle;
    private final RepositorioReserva repositorioReserva;
    private final RepositorioMuelle repositorioMuelle;
    private final ServicioUsuarioImp servicioUsuarioImp;
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioReserva servicioReserva;
    private final RepositorioPedido repositorioPedido;
    private final ServicioPedido servicioPedido;


    // TODO: falta quiza
    @PostMapping("/subir-csv-pedidos")
    public List<Muelle> uploadCSVPedidos(@RequestParam("file") MultipartFile file) {
        List<Muelle> muelles;


        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "fichero vacio");
        } else {

            // parse CSV file to create a list of `User` objects
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                // create csv bean reader
                CsvToBean<Muelle> csvToBean = new CsvToBeanBuilder(reader)
                        .withType(Muelle.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .build();

                // convert `CsvToBean` object to list of users
                muelles = csvToBean.parse();

                // TODO: save users in DB?

            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no se ha podido leer el csv");
            }
        }

        return muelles;
    }



    @GetMapping("/pedidos_hasta_ahora")
    public Collection<Pedido> pedidos(){
        List<Pedido> ret = repositorioPedido.findByEstadoEquals("cargado");
        ret.addAll(repositorioPedido.findByEstadoEquals("descargado"));
        ret.removeIf(p -> p.getHoraEntrada()!= null && !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));

        return ret;
    }

    @GetMapping("/pedidos_dia")
    public Collection <PedidosDiaDTO> pedidosDia(){
        List<Pedido> pedidos = repositorioPedido.findByEstadoEquals("cargado");
        pedidos.addAll(repositorioPedido.findByEstadoEquals("descargado"));
        pedidos.removeIf(p -> p.getHoraEntrada()!= null && !(p.getHoraEntrada().atZone(ZoneOffset.UTC).getDayOfYear() == SimulateClock.getMomentoSimulacion().atOffset(ZoneOffset.UTC).getDayOfYear()));

        List<Muelle> muelles = repositorioMuelle.findAll();
        List<PedidosDiaDTO> ret = new ArrayList<>();

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
                ret.add(new PedidosDiaDTO(nombre,lista));
        }

        return ret;
    }
}
