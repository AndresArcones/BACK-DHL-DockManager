package com.andres.Proyecto_Fin_de_Grado.Service;

import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Repository.RepositorioUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ServicioMuelle {
    private final RepositorioMuelle repositorioMuelle;


    public void crearMuelles(int numeroMuelles){
        for (int i =0; i<numeroMuelles; i++){
            Muelle muelle = new Muelle("descarga", "trailer", 7, "disponible");
            repositorioMuelle.save(muelle);
        }
    }


    public Collection<Muelle> mostrarMuelles(){
        return repositorioMuelle.findAll();
    }

    public Muelle muelle(String id){
        return repositorioMuelle.findById(id).get();
    }



}
