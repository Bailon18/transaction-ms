package com.paucar.transaction_ms.error;

import feign.FeignException;

public class ErrorHandler {

    public static String extraerMensajeError(FeignException e) {
        try {
            String jsonError = e.contentUTF8();
            if (jsonError != null && jsonError.contains("\"mensaje\"")) {
                return jsonError.split("\"mensaje\":\"")[1].split("\"")[0];
            }
        } catch (Exception ex) {
            System.out.println("Error al parsear el mensaje JSON: " + ex.getMessage());
        }
        return "Error desconocido";
    }
}
