package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioRol;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioUsuario;
import com.andres.Proyecto_Fin_de_Grado.utilidades.Validador;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioUsuarioImp implements ServicioUsuario, UserDetailsService {
    private final RepositorioRol repositorioRol;
    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder passwordEncoder;




    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        if(!Validador.isEmailValid(usuario.getUsuario())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"email no valido");
        }else if(repositorioUsuario.findByUsuario(usuario.getUsuario()) == null){

            usuario.setContrasenia(passwordEncoder.encode(usuario.getContrasenia())); //encriptamos la contraseña con el mismo codificador usado en JWT
            stream(usuario.getRoles().toArray(new Rol[usuario.getRoles().size()])).forEach(rol -> {
                rol.setId(repositorioRol.findByNombre(rol.getNombre()).getId());
            });
            return repositorioUsuario.save(usuario);
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT,"usuario ya existe");
        }


    }

    @Override
    public Rol guardarRol(Rol rol) {
        if(repositorioRol.findByNombre(rol.getNombre()) == null){
            return repositorioRol.save(rol);
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT,"rol ya existe");
        }

    }

    @Override
    public void setRolAUsuario(String nombreUsuario, String nombreRol) {
        Usuario user = repositorioUsuario.findByUsuario(nombreUsuario);
        Rol rol = repositorioRol.findByNombre(nombreRol);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"user no existe");
        }
        if(rol == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"rol no existe");
        }
        if(user.getRoles() != null)
            user.getRoles().add(rol);
        else {
            user.setRoles(new ArrayList<Rol>());
            user.getRoles().add(rol);
        }
        repositorioUsuario.save(user);
    }

    @Override
    public Usuario getUsuarioPorNombreUsuario(String usuario) {
        return repositorioUsuario.findByUsuario(usuario);
    }

    @Override
    public void actualizarNombre(Usuario newUsuario){
        Usuario usuario = repositorioUsuario.findByUsuario(newUsuario.getUsuario());
        if(usuario == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Usuario no encontrado");
        }
        if(newUsuario.getNombre() != null)
            usuario.setNombre(newUsuario.getNombre());

        repositorioUsuario.save(usuario);

    }

    @Override
    public void actualizarApellidos(Usuario newUsuario) {
        Usuario usuario = repositorioUsuario.findByUsuario(newUsuario.getUsuario());
        if(usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        if(newUsuario.getApellidos() != null)
            usuario.setApellidos(newUsuario.getApellidos());

        repositorioUsuario.save(usuario);
    }

    @Override
    public void actualizarContrasenia(Usuario newUsuario) {
        Usuario usuario = repositorioUsuario.findByUsuario(newUsuario.getUsuario());
        if(usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        if(newUsuario.getContrasenia() != null)
            usuario.setContrasenia(passwordEncoder.encode(newUsuario.getContrasenia())); //encriptamos la contraseña con el mismo codificador usado en JWT

        repositorioUsuario.save(usuario);
    }

    @Override
    public List<Usuario> getUsuarios() {
        return repositorioUsuario.findAll();
    }

    //spring security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByUsuario(username);
        if(usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        //if no confirmado email mandar error, revisar email y confirmar
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        usuario.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getNombre()));
        });

        return new org.springframework.security.core.userdetails.User(usuario.getUsuario(),usuario.getContrasenia(), authorities);
    }
}
