package com.andres.Proyecto_Fin_de_Grado.Model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
@NoArgsConstructor
public class Reserva implements Comparable<Reserva>{
    @Id
    private String id;
    private String idMuelle;
    private String nombreMuelle;
    private String dni; //form
    private String matricula; //form
    private String idPedido; //form (6 digitos)
    private String actividad; //carga o descarga
    private Instant fechaHoraReserva;
    private String tipoCamion;

    public Reserva(String idMuelle,String nombreMuelle, String dni, String matricula, String idPedido, String actividad, Instant fechaHoraReserva, String tipoCamion) {
        this.idMuelle = idMuelle;
        this.nombreMuelle = nombreMuelle;
        this.dni = dni;
        this.matricula = matricula;
        this.idPedido = idPedido;
        this.actividad = actividad;
        this.fechaHoraReserva = fechaHoraReserva;
        this.tipoCamion = tipoCamion;
    }

    @Override
    public int compareTo(Reserva reserva) {
        if (getFechaHoraReserva() == null || reserva.getFechaHoraReserva() == null)
            return 0;

        return getFechaHoraReserva().compareTo(reserva.getFechaHoraReserva());
    }

}
