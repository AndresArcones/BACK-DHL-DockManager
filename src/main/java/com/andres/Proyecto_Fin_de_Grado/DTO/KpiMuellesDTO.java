package com.andres.Proyecto_Fin_de_Grado.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class KpiMuellesDTO {
        private String nombre;
        private double porUso;

        public KpiMuellesDTO(String nombre, double porUso){
                this.nombre = nombre;
                this.porUso = porUso;
        }
}
