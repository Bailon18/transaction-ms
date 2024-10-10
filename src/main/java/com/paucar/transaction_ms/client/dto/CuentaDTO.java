package com.paucar.transaction_ms.client.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CuentaDTO {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private TipoCuenta tipoCuenta;
    private Long clienteId;
    private EstadoCuenta estado;

}
