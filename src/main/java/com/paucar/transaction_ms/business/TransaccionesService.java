package com.paucar.transaction_ms.business;


import com.paucar.transaction_ms.model.TransactionResponse;

import java.util.List;

public interface TransaccionesService {

    List<TransactionResponse> obtenerHistorial();
    TransactionResponse registrarDeposito(String cuentaDestino, Double monto);
    TransactionResponse registrarRetiro(String cuentaOrigen, Double monto);
    TransactionResponse registrarTransferencia(String cuentaOrigen, String cuentaDestino, Double monto) ;
}
