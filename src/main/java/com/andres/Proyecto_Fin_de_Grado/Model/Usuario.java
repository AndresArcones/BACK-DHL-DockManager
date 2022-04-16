package com.andres.Proyecto_Fin_de_Grado.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Data
@Document
@NoArgsConstructor
public class Usuario {
    @Id
    private String id;
    private String nombre;
    private String apellidos;
    private String usuario; //mail
    private String contrasenia;
    private Collection<Rol> roles = new HashSet<>();
    private Collection<Reserva> reservas = new HashSet<>();

    public Usuario(String nombre, String apellidos, String usuario, String contrasenia, Collection<Rol> roles) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.roles = roles;
    }

    public boolean anularReserva(Reserva reserva){
        if(reserva==null)
            return false;

        return this.reservas.remove(reserva);
    }


}
