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
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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


    @PostMapping("/subir-csv-pedido")
    public List<String[]> uploadCSVPedidos(@RequestParam("file") MultipartFile file) {
        List<Muelle> muelles;
        List<String[]> lineas = null;


        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "fichero vacio");
        } else {


            try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                lineas = reader.readAll();
                lineas.remove(0);
                if(lineas.size() == 0) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se encontraron pedidos en el fichero");
                List<String[]> datos = Arrays.stream(lineas.toArray(new String[lineas.size()][])).collect(Collectors.toList());

                for (String[] pedido: datos) {
                    Pedido ped = new Pedido();
                    ped.setId(pedido[1]);
                    servicioPedido.guardarPedido(ped);
                }

                //System.out.println(datos);*/



            } catch (IOException | CsvException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no se ha podido leer el csv");
            }catch (ResponseStatusException ex){
                throw ex;
            }
        }

        return lineas;
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
