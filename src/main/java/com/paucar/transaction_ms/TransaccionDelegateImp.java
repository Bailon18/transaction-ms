package com.paucar.transaction_ms;

import com.paucar.transaction_ms.business.TransaccionesService;

import com.paucar.transaction_ms.api.TransaccionesApiDelegate;
import com.paucar.transaction_ms.model.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionDelegateImp implements TransaccionesApiDelegate {

    private final TransaccionesService transaccionesService;

    @Override
    public ResponseEntity<List<TransactionResponse>> obtenerHistorial() {
        return ResponseEntity.ok(this.transaccionesService.obtenerHistorial());
    }

    @Override
    public ResponseEntity<TransactionResponse> registrarDeposito(String cuentaDestino, Double monto) {
        return ResponseEntity.ok(this.transaccionesService.registrarDeposito(cuentaDestino, monto));
    }

    @Override
    public ResponseEntity<TransactionResponse> registrarRetiro(String cuentaOrigen, Double monto) {
        return ResponseEntity.ok(this.transaccionesService.registrarRetiro(cuentaOrigen, monto));
    }

    @Override
    public ResponseEntity<TransactionResponse> registrarTransferencia(String cuentaOrigen, String cuentaDestino,
                                                                 Double monto) {
        return ResponseEntity.ok(this.transaccionesService.registrarTransferencia(cuentaOrigen, cuentaDestino, monto));
    }

}
