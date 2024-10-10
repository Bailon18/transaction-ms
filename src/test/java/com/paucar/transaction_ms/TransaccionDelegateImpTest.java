package com.paucar.transaction_ms;

import com.paucar.transaction_ms.business.TransaccionesService;
import com.paucar.transaction_ms.model.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TransaccionDelegateImpTest {

    private static final Logger log = LoggerFactory.getLogger(TransaccionDelegateImpTest.class);

    @Mock
    private TransaccionesService transaccionesService;

    @InjectMocks
    private TransaccionDelegateImp transaccionDelegate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerHistorial_DeberiaRetornarListaDeTransacciones() {
        log.info("Iniciando prueba: obtenerHistorial_DeberiaRetornarListaDeTransacciones");

        TransactionResponse transaccion1 = new TransactionResponse();
        transaccion1.setId("trans1");
        transaccion1.setTipo("DEPOSITO");
        transaccion1.setMonto(1000.0);
        transaccion1.setFecha(LocalDate.now());
        transaccion1.setCuentaDestino("12345678");

        TransactionResponse transaccion2 = new TransactionResponse();
        transaccion2.setId("trans2");
        transaccion2.setTipo("RETIRO");
        transaccion2.setMonto(500.0);
        transaccion2.setFecha(LocalDate.now());
        transaccion2.setCuentaOrigen("87654321");

        List<TransactionResponse> historialEsperado = Arrays.asList(transaccion1, transaccion2);

        when(transaccionesService.obtenerHistorial()).thenReturn(historialEsperado);

        ResponseEntity<List<TransactionResponse>> resultado = transaccionDelegate.obtenerHistorial();

        log.info("Resultado obtenido: {}", resultado.getBody());
        assertNotNull(resultado.getBody(), "El resultado no debería ser nulo.");
        assertEquals(2, resultado.getBody().size(), "El historial debería tener 2 transacciones.");
        verify(transaccionesService, times(1)).obtenerHistorial();
    }

    @Test
    void registrarDeposito_DeberiaRegistrarDepositoExitosamente() {
        log.info("Iniciando prueba: registrarDeposito_DeberiaRegistrarDepositoExitosamente");

        String cuentaDestino = "12345678";
        Double monto = 1000.0;

        TransactionResponse depositoEsperado = new TransactionResponse();
        depositoEsperado.setId("dep1");
        depositoEsperado.setTipo("DEPOSITO");
        depositoEsperado.setMonto(monto);
        depositoEsperado.setFecha(LocalDate.now());
        depositoEsperado.setCuentaDestino(cuentaDestino);

        when(transaccionesService.registrarDeposito(cuentaDestino, monto)).thenReturn(depositoEsperado);

        ResponseEntity<TransactionResponse> resultado = transaccionDelegate.registrarDeposito(cuentaDestino, monto);

        log.info("Resultado obtenido: {}", resultado.getBody());
        assertNotNull(resultado.getBody(), "El resultado no debería ser nulo.");
        assertEquals(depositoEsperado, resultado.getBody(), "El depósito debería coincidir con el valor esperado.");
        verify(transaccionesService, times(1)).registrarDeposito(cuentaDestino, monto);
    }

    @Test
    void registrarRetiro_DeberiaRegistrarRetiroExitosamente() {
        log.info("Iniciando prueba: registrarRetiro_DeberiaRegistrarRetiroExitosamente");

        String cuentaOrigen = "87654321";
        Double monto = 500.0;

        TransactionResponse retiroEsperado = new TransactionResponse();
        retiroEsperado.setId("ret1");
        retiroEsperado.setTipo("RETIRO");
        retiroEsperado.setMonto(monto);
        retiroEsperado.setFecha(LocalDate.now());
        retiroEsperado.setCuentaOrigen(cuentaOrigen);

        when(transaccionesService.registrarRetiro(cuentaOrigen, monto)).thenReturn(retiroEsperado);

        ResponseEntity<TransactionResponse> resultado = transaccionDelegate.registrarRetiro(cuentaOrigen, monto);

        log.info("Resultado obtenido: {}", resultado.getBody());
        assertNotNull(resultado.getBody(), "El resultado no debería ser nulo.");
        assertEquals(retiroEsperado, resultado.getBody(), "El retiro debería coincidir con el valor esperado.");
        verify(transaccionesService, times(1)).registrarRetiro(cuentaOrigen, monto);
    }

    @Test
    void registrarTransferencia_DeberiaRegistrarTransferenciaExitosamente() {
        log.info("Iniciando prueba: registrarTransferencia_DeberiaRegistrarTransferenciaExitosamente");

        String cuentaOrigen = "87654321";
        String cuentaDestino = "12345678";
        Double monto = 500.0;

        TransactionResponse transferenciaEsperada = new TransactionResponse();
        transferenciaEsperada.setId("tran1");
        transferenciaEsperada.setTipo("TRANSFERENCIA");
        transferenciaEsperada.setMonto(monto);
        transferenciaEsperada.setFecha(LocalDate.now());
        transferenciaEsperada.setCuentaOrigen(cuentaOrigen);
        transferenciaEsperada.setCuentaDestino(cuentaDestino);

        when(transaccionesService.registrarTransferencia(cuentaOrigen, cuentaDestino, monto)).thenReturn(transferenciaEsperada);

        ResponseEntity<TransactionResponse> resultado = transaccionDelegate.registrarTransferencia(cuentaOrigen, cuentaDestino, monto);

        log.info("Resultado obtenido: {}", resultado.getBody());
        assertNotNull(resultado.getBody(), "El resultado no debería ser nulo.");
        assertEquals(transferenciaEsperada, resultado.getBody(), "La transferencia debería coincidir con el valor esperado.");
        verify(transaccionesService, times(1)).registrarTransferencia(cuentaOrigen, cuentaDestino, monto);
    }
}
