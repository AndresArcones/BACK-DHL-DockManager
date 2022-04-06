package com.andres.Proyecto_Fin_de_Grado.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Document
@NoArgsConstructor
public class Muelle {
    @Id
    private String id;
    private String tipoMuelle;
    private String tipoCamion;
    private int numeroTramosReserva; //revisar: numero de tramos de reserva
    private String estado;
    private Reserva[] reservas;


    public Muelle(String tipoMuelle, String tipoCamion, int numeroTramosReserva, String estado) {
        this.tipoMuelle = tipoMuelle;
        this.tipoCamion = tipoCamion;
        this.numeroTramosReserva = numeroTramosReserva;
        this.estado = estado;
        this.reservas = new Reserva[numeroTramosReserva];
    }
}
