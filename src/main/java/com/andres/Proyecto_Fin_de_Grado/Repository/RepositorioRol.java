package com.andres.Proyecto_Fin_de_Grado.Repository;

import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioRol extends MongoRepository<Rol, String> {
    Rol findByNombre(String nombre);
}
