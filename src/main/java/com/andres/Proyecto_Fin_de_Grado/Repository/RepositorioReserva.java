package com.andres.Proyecto_Fin_de_Grado.Repository;

import com.andres.Proyecto_Fin_de_Grado.Model.Reserva;
import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositorioReserva extends MongoRepository<Reserva, String> {
    List<Reserva> findByMatriculaEquals(String matricula);
}
