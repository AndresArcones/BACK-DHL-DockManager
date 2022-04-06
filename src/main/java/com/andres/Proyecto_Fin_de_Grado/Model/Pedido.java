package com.andres.Proyecto_Fin_de_Grado.Model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@NoArgsConstructor
public class Pedido {
    @Id
    private String id;
    private Date hora_entrada;
    private Date hora_salida;
    private String estado;
}
