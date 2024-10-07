package com.paucar.transaction_ms.model.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class TransaccionDTO {

    private String id;
    private String tipo;
    private Double monto;
    private LocalDate fecha;
    private String cuentaOrigen;
    private String cuentaDestino;

}
