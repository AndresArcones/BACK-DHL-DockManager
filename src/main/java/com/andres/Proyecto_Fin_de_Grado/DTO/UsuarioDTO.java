package com.andres.Proyecto_Fin_de_Grado.DTO;

import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import lombok.Data;

import java.util.Collection;

@Data
public class UsuarioDTO {
    private String nombre;
    private String apellidos;
    private String usuario; //mail
    private String contrasenia;
    private Collection<Rol> roles;
}
