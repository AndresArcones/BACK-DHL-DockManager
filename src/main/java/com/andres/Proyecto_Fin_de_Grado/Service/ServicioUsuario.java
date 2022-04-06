package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;

import java.util.List;

public interface ServicioUsuario {
    Usuario guardarUsuario(Usuario usuario);
    Usuario getUsuarioPorNombreUsuario(String nombre);
    void actualizarNombre(Usuario usuario);
    void actualizarApellidos(Usuario usuario);
    void actualizarContrasenia(Usuario usuario);
    List<Usuario> getUsuarios();
    Rol guardarRol(Rol rol);
    void setRolAUsuario(String email, String nombreRol);






}
