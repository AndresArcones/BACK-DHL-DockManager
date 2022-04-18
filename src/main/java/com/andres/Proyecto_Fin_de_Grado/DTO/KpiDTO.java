package com.andres.Proyecto_Fin_de_Grado.DTO;

import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class KpiDTO {
        private double porReservasCanceldasMes;
        private double porRetrasosLllegada;
        Map<String, Double> porUtilizacionMuelles;
        public KpiDTO(double porReservasCanceldasMes, double porRetrasosLllegada, Map<String, Double> porUtilizacionMuelles){
                this.porReservasCanceldasMes = porReservasCanceldasMes;
                this.porRetrasosLllegada = porRetrasosLllegada;
                this.porUtilizacionMuelles = porUtilizacionMuelles;
        }
}
