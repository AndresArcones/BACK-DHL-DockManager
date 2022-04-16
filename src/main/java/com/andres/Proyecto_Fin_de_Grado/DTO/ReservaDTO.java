package com.andres.Proyecto_Fin_de_Grado.DTO;

import com.andres.Proyecto_Fin_de_Grado.Model.Rol;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Collection;

@Data
@NoArgsConstructor
public class ReservaDTO {
        //private String idMuelle; ya viene en path
        private String dni; //form
        private String matricula; //form
        private String idPedido; //form (6 digitos)
        private String actividad; //carga o descarga
        //private Instant fechaHoraReserva;
        private String fechaHoraReserva;
        private String tipoCamion;
        private int tramoHora;

}
