package com.andres.Proyecto_Fin_de_Grado.Repository;

import com.andres.Proyecto_Fin_de_Grado.Model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RepositorioUsuario extends MongoRepository<Usuario, String> {
    Optional<Usuario> findById(String id);
    Usuario findByNombre(String nombre);
    Usuario findByUsuario (String usuario);
}
