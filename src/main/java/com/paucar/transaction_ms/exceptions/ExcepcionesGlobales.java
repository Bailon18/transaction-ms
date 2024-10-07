package com.paucar.transaction_ms.exceptions;


import com.paucar.transaction_ms.model.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExcepcionesGlobales {

    @ExceptionHandler(TransferenciaException.class)
    public ResponseEntity<ApiResponse<Void>> manejarExcepcionTransferencia(TransferenciaException ex) {
        ApiResponse<Void> respuestaError = ApiResponse.<Void>builder()
                .estado(HttpStatus.BAD_REQUEST.value())
                .mensaje(ex.getMessage())
                .datos(null)
                .build();
        return new ResponseEntity<>(respuestaError, HttpStatus.BAD_REQUEST);
    }
}
