package com.andres.Proyecto_Fin_de_Grado.utilidades;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWT {
    private String nombreUsuario;
    private String[] roles;
}
