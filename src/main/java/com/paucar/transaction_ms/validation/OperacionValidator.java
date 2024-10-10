package com.paucar.transaction_ms.validation;

import com.paucar.transaction_ms.model.dto.ApiResponse;
import com.paucar.transaction_ms.exceptions.TransferenciaException;

public class OperacionValidator {

    public static void validarOperacion(ApiResponse<?> respuesta, String mensajeError) {
        if (respuesta == null || respuesta.getDatos() == null) {
            throw new TransferenciaException(mensajeError);
        }
    }
}
