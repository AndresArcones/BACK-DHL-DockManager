package com.andres.Proyecto_Fin_de_Grado.Controller;


import com.andres.Proyecto_Fin_de_Grado.DTO.UsuarioDTO;
import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuarioImp;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.stream;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UsuarioController {
    private final ServicioUsuarioImp servicioUsuario;
    private ModelMapper mapper;

    //ADMIN
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Usuario> mostrarUsuarios(){
        return servicioUsuario.getUsuarios();
    }

    //ALL
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void aniadirUsuario(@RequestBody UsuarioDTO usuarioDTO){
        Rol userRole = new Rol();
        userRole.setNombre("ROL_USER");
        usuarioDTO.getRoles().add(userRole);
        Usuario usuario = new Usuario(usuarioDTO.getNombre(), usuarioDTO.getApellidos(), usuarioDTO.getUsuario(), usuarioDTO.getContrasenia(), usuarioDTO.getRoles());
        servicioUsuario.guardarUsuario(usuario);

    }

    //ADMIN
    @PostMapping("/role/save")
    public void aniadirRol(@RequestBody Rol rol){
        servicioUsuario.guardarRol(rol);
    }

    //ADMIN
    @PostMapping("/role/addtouser")
    @ResponseStatus(HttpStatus.OK)
    public void aniadirRolAUsuario(@RequestBody FormularioUsuarioRol formularioUsuarioRol){
        servicioUsuario.setRolAUsuario(formularioUsuarioRol.getUsuario(), formularioUsuarioRol.getRol());
    }

    //USER, ADMIN (solo si editas tu usuario)
    @PutMapping("/user/update/pass")
    public void actualizarContraseniaUsuario(@RequestBody Usuario usuario){ //<--
        servicioUsuario.actualizarContrasenia(usuario);
    }

    //USER, ADMIN (solo si editas tu usuario)
    @PutMapping("/user/update/username")
    public void actualizarNombreUsuario(@RequestBody Usuario usuario){
        servicioUsuario.actualizarNombre(usuario);
    }

}

@Data
class FormularioUsuarioRol {
    private String usuario;
    private String rol;
}

