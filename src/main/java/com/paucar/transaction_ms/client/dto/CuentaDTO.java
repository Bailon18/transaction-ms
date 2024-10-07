package com.paucar.transaction_ms.client.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuentaDTO {

    private Long id;
    private String numeroCuenta;
    private Double saldo;
    private String tipoCuenta;
    private Long clienteId;
    private String estado;

}
