package com.andres.Proyecto_Fin_de_Grado.Model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Data
@Document
@NoArgsConstructor
public class Pedido {
    private String id;
    private Instant horaEntrada;
    private Instant horaSalida;
    private String estado = "no entregado";
    private String matricula;
    private String tipoPedido;
    private long retraso = 0;
    private long tiempoTardado = -1;

}
