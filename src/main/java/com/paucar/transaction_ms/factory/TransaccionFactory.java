package com.paucar.transaction_ms.factory;

import com.paucar.transaction_ms.model.entity.Transaccion;

import java.time.LocalDate;

public class TransaccionFactory {

    public static Transaccion crearTransaccion(String tipo, Double monto, String cuentaOrigen, String cuentaDestino) {
        return Transaccion.builder()
                .tipo(tipo)
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .build();
    }
}
