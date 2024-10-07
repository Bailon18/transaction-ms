package com.paucar.transaction_ms.client;


import com.paucar.transaction_ms.client.dto.CuentaDTO;
import com.paucar.transaction_ms.model.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Quitar el parámetro 'url' al manejarse localmente
@FeignClient(name = "ACCOUNT-MS", url = "https://account-ms-production.up.railway.app")
public interface CuentaFeign {

    @PutMapping("/cuentas/realizar-transferencia")
    ResponseEntity<ApiResponse<Boolean>> transferencia(
            @RequestParam("cuentaOrigen") String cuentaOrigen,
            @RequestParam("cuentaDestino") String cuentaDestino,
            @RequestParam("monto") Double monto
    );

    @PutMapping("/cuentas/retirar")
    ResponseEntity<ApiResponse<CuentaDTO>> retirar(@RequestParam("numeroCuenta") String numeroCuenta,
                                                     @RequestParam("monto") Double mont
    );

    @PutMapping("/cuentas/depositar")
    ResponseEntity<ApiResponse<CuentaDTO>> depositar(@RequestParam("numeroCuenta") String numeroCuenta,
                                                     @RequestParam("monto") Double mont
    );

}
