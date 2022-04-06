package com.andres.Proyecto_Fin_de_Grado.Repository;

import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RepositorioMuelle extends MongoRepository<Muelle, String> {
    Optional<Muelle> findById(String id);
}
