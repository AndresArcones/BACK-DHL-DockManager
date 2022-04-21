package com.andres.Proyecto_Fin_de_Grado.Repository;

import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositorioPedido extends MongoRepository<Pedido, String> {
    List<Pedido> findByRetrasoGreaterThan(int num);
    List<Pedido> findByEstadoEquals(String estado);
}
