package com.andres.Proyecto_Fin_de_Grado.Controller;

import com.andres.Proyecto_Fin_de_Grado.Model.Muelle;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioMuelle;
import com.andres.Proyecto_Fin_de_Grado.Service.ServicioUsuarioImp;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class MuelleController {
    private final ServicioMuelle servicioMuelle;


    //USER
    @GetMapping("/muelles")
    public Collection<Muelle> mostrarMuelles(){
        return servicioMuelle.mostrarMuelles();
    }

    //USER
    @GetMapping("/muelle/{mulleId}")
    public Muelle muelle(@PathVariable String mulleId){
        return servicioMuelle.muelle(mulleId);
    }




}
