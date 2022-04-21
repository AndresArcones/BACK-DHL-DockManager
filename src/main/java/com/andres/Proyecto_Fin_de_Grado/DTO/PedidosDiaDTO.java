package com.andres.Proyecto_Fin_de_Grado.DTO;

import com.andres.Proyecto_Fin_de_Grado.Model.Pedido;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class PedidosDiaDTO {
        private String nombre;
        private Collection<Pedido> pedidos;

        public PedidosDiaDTO(String nombre, Collection<Pedido> pedidos){
                this.nombre = nombre;
                this.pedidos = pedidos;
        }
}
