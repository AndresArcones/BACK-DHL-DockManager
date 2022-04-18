package com.andres.Proyecto_Fin_de_Grado.Model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

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

    public boolean anularReserva(Reserva reserva){
        if(reserva==null)
            return false;

        boolean ret = false;

        for(int i =0;i<this.reservas.length;i++)
            if (reserva.equals(this.reservas[i])){
                this.reservas[i] = null;
                ret = true;
            }

        return ret;
    }

    public double PorcentajeUso(){
        return (double) Arrays.stream(reservas).filter(Objects::nonNull).count()*100/reservas.length;
    }
}
