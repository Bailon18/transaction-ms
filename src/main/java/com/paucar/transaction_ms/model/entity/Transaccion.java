package com.paucar.transaction_ms.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@Document(collection = "transaccion")
public class Transaccion {

    @Id
    private String id;

    private String tipo;
    private Double monto;
    private LocalDate fecha;
    private String cuentaOrigen;
    private String cuentaDestino;
}
