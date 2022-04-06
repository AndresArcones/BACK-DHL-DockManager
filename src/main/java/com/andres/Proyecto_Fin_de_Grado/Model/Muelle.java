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
    private String nombre;
    private String tipoMuelle;
    private String tipoCamion;
    private int aperturaMuelle; //cuando abre el muelle
    private int numeroTramosReserva; //numero de tramos (horas) de reserva
    private String estado;
    private Reserva[] reservas;


    public Muelle(String nombre,String tipoMuelle, String tipoCamion, int aperturaMuelle,int numeroTramosReserva, String estado) {
        this.tipoMuelle = tipoMuelle;
        this.tipoCamion = tipoCamion;
        this.aperturaMuelle = aperturaMuelle;
        this.numeroTramosReserva = numeroTramosReserva;
        this.estado = estado;
        this.reservas = new Reserva[numeroTramosReserva];
        this.nombre = nombre;
    }
}
