package com.andres.Proyecto_Fin_de_Grado.utilidades;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SimulateClock {
    private static Instant momentoSimulacion = LocalDateTime.now().toInstant(ZoneOffset.of("+00:00"));
    private static long tiempoSimuladoSegundos = 0;
    private static boolean ahora;




    public static LocalDateTime getDateAndTime(){
        long tiempoSimulacionSegundos = LocalDateTime.now().toInstant(ZoneOffset.of("+00:00")).getEpochSecond() - momentoSimulacion.getEpochSecond() + tiempoSimuladoSegundos;
        return LocalDateTime.ofEpochSecond(tiempoSimulacionSegundos, 0, ZoneOffset.of("+00:00") );
    }

    public static Instant getMomentoSimulacion() {
        if (ahora)
            return LocalDateTime.now().toInstant(ZoneOffset.of("+00:00"));
        else
            return momentoSimulacion;
    }

    public static void setMomentoSimulacion(Instant momentoSimulacion) {
        SimulateClock.momentoSimulacion = momentoSimulacion;
    }

    public static long getTiempoSimuladoSegundos() { return tiempoSimuladoSegundos; }

    public static void setTiempoSimuladoSegundos(long tiempoSimuladoSegundos) {
        SimulateClock.tiempoSimuladoSegundos = tiempoSimuladoSegundos;
        SimulateClock.momentoSimulacion = LocalDateTime.now().toInstant(ZoneOffset.of("+00:00"));
    }

    public static void setAhora(boolean flag) {
        ahora = flag;
    }
}
