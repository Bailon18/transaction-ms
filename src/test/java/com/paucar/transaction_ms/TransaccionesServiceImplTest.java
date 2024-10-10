package com.paucar.transaction_ms;

import com.paucar.transaction_ms.business.TransaccionesServiceImpl;
import com.paucar.transaction_ms.business.mapper.TransaccionMapper;
import com.paucar.transaction_ms.client.CuentaFeign;
import com.paucar.transaction_ms.client.dto.CuentaDTO;
import com.paucar.transaction_ms.client.dto.EstadoCuenta;
import com.paucar.transaction_ms.client.dto.TipoCuenta;
import com.paucar.transaction_ms.model.TransactionResponse;
import com.paucar.transaction_ms.model.dto.ApiResponse;
import com.paucar.transaction_ms.model.entity.Transaccion;
import com.paucar.transaction_ms.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransaccionesServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(TransaccionesServiceImplTest.class);

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private TransaccionMapper transaccionMapper;

    @Mock
    private CuentaFeign cuentaFeign;

    @InjectMocks
    private TransaccionesServiceImpl transaccionesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerHistorial_DeberiaRetornarListaDeTransacciones() {
        log.info("Iniciando prueba: obtenerHistorial_DeberiaRetornarListaDeTransacciones");

        Transaccion transaccion1 = Transaccion.builder()
                .id("trans1")
                .tipo("DEPOSITO")
                .monto(1000.0)
                .fecha(LocalDate.now())
                .cuentaDestino("12345678")
                .build();

        Transaccion transaccion2 = Transaccion.builder()
                .id("trans2")
                .tipo("RETIRO")
                .monto(500.0)
                .fecha(LocalDate.now())
                .cuentaOrigen("87654321")
                .build();

        when(transaccionRepository.findAll()).thenReturn(List.of(transaccion1, transaccion2));

        TransactionResponse response1 = new TransactionResponse();
        TransactionResponse response2 = new TransactionResponse();
        when(transaccionMapper.convertirTransaccionResponse(transaccion1)).thenReturn(response1);
        when(transaccionMapper.convertirTransaccionResponse(transaccion2)).thenReturn(response2);

        List<TransactionResponse> resultado = transaccionesService.obtenerHistorial();

        log.info("Resultado obtenido: {}", resultado);

        assertEquals(2, resultado.size(), "El historial debería tener 2 transacciones.");
        verify(transaccionRepository, times(1)).findAll();
    }

    @Test
    void registrarDeposito_CuandoDepositoExitoso_DeberiaRegistrarDeposito() {
        log.info("Iniciando prueba: registrarDeposito_CuandoDepositoExitoso_DeberiaRegistrarDeposito");


        String cuentaDestino = "12345678";
        Double monto = 1000.0;
        String cuentaOrigen = "87654321";

        Transaccion transaccion = Transaccion.builder()
                .id("dep1")
                .tipo("DEPOSITO")
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaDestino(cuentaDestino)
                .build();

        CuentaDTO cuentaDTO = new CuentaDTO(1L, cuentaOrigen, 1000.0, TipoCuenta.AHORROS, 1L, EstadoCuenta.ACTIVO);

        ApiResponse<CuentaDTO> apiResponse = ApiResponse.<CuentaDTO>builder()
                .mensaje("Depósito exitoso")
                .datos(cuentaDTO)
                .build();

        when(cuentaFeign.depositar(cuentaDestino, monto)).thenReturn(ResponseEntity.ok(apiResponse));


        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);


        TransactionResponse transactionResponseEsperado = new TransactionResponse();
        transactionResponseEsperado.setId("dep1");
        transactionResponseEsperado.setTipo("DEPOSITO");
        transactionResponseEsperado.setMonto(monto);
        transactionResponseEsperado.setFecha(LocalDate.now());
        transactionResponseEsperado.setCuentaDestino(cuentaDestino);

        when(transaccionMapper.convertirTransaccionResponse(any(Transaccion.class))).thenReturn(transactionResponseEsperado);


        TransactionResponse resultado = transaccionesService.registrarDeposito(cuentaDestino, monto);

        log.info("Resultado obtenido para depósito: {}", resultado);

        assertNotNull(resultado, "El resultado de la transacción no debería ser nulo.");
        assertEquals(transactionResponseEsperado, resultado, "El resultado debería coincidir con el valor esperado.");

        verify(cuentaFeign, times(1)).depositar(cuentaDestino, monto);
        verify(transaccionRepository, times(1)).save(any(Transaccion.class)); // Aquí se ignora el `id`
        verify(transaccionMapper, times(1)).convertirTransaccionResponse(any(Transaccion.class));
    }




    @Test
    void registrarRetiro_CuandoRetiroExitoso_DeberiaRegistrarRetiro() {
        log.info("Iniciando prueba: registrarRetiro_CuandoRetiroExitoso_DeberiaRegistrarRetiro");


        String cuentaOrigen = "87654321";
        Double monto = 500.0;


        Transaccion transaccion = Transaccion.builder()
                .id("ret1")
                .tipo("RETIRO")
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaOrigen(cuentaOrigen)
                .build();


        CuentaDTO cuentaDTO = new CuentaDTO(1L, cuentaOrigen, 1000.0, TipoCuenta.AHORROS, 1L, EstadoCuenta.ACTIVO);
        ApiResponse<CuentaDTO> apiResponse = ApiResponse.<CuentaDTO>builder()
                .mensaje("Retiro exitoso")
                .datos(cuentaDTO)
                .build();


        when(cuentaFeign.retirar(cuentaOrigen, monto)).thenReturn(ResponseEntity.ok(apiResponse));

        log.info("Mockeando respuesta Feign con datos: {}", apiResponse);

        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccion);

        TransactionResponse transaccionResponse = new TransactionResponse();
        transaccionResponse.setId("ret1");
        transaccionResponse.setTipo("RETIRO");
        transaccionResponse.setMonto(monto);
        transaccionResponse.setFecha(LocalDate.now());
        transaccionResponse.setCuentaOrigen(cuentaOrigen);
        when(transaccionMapper.convertirTransaccionResponse(any(Transaccion.class))).thenReturn(transaccionResponse);

        TransactionResponse resultado = transaccionesService.registrarRetiro(cuentaOrigen, monto);

        log.info("Resultado obtenido para retiro: {}", resultado);
        assertNotNull(resultado, "El resultado de la transacción no debería ser nulo.");

        verify(cuentaFeign, times(1)).retirar(cuentaOrigen, monto);
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
        verify(transaccionMapper, times(1)).convertirTransaccionResponse(any(Transaccion.class));
    }


    @Test
    void registrarTransferencia_CuandoTransferenciaExitoso_DeberiaRegistrarTransferencia() {
        log.info("Iniciando prueba: registrarTransferencia_CuandoTransferenciaExitoso_DeberiaRegistrarTransferencia");

        String cuentaOrigen = "87654321";
        String cuentaDestino = "12345678";
        Double monto = 500.0;

        Transaccion transaccion = Transaccion.builder()
                .id(null)
                .tipo("TRANSFERENCIA")
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .build();

        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .mensaje("Transferencia exitosa")
                .datos(true)
                .build();
        when(cuentaFeign.transferencia(cuentaOrigen, cuentaDestino, monto)).thenReturn(ResponseEntity.ok(apiResponse));

        TransactionResponse transactionResponseEsperado = new TransactionResponse();
        transactionResponseEsperado.setId("tran1");
        transactionResponseEsperado.setTipo("TRANSFERENCIA");
        transactionResponseEsperado.setMonto(monto);
        transactionResponseEsperado.setFecha(LocalDate.now());
        transactionResponseEsperado.setCuentaOrigen(cuentaOrigen);
        transactionResponseEsperado.setCuentaDestino(cuentaDestino);

        when(transaccionMapper.convertirTransaccionResponse(any(Transaccion.class))).thenReturn(transactionResponseEsperado);

        Transaccion transaccionGuardada = Transaccion.builder()
                .id("tran1")
                .tipo("TRANSFERENCIA")
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .build();
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionGuardada);

        TransactionResponse resultado = transaccionesService.registrarTransferencia(cuentaOrigen, cuentaDestino, monto);

        log.info("Resultado obtenido para transferencia: {}", resultado);

        assertNotNull(resultado, "El resultado de la transferencia no debería ser nulo.");
        assertEquals(transactionResponseEsperado, resultado, "El resultado debería coincidir con el valor esperado.");

        verify(cuentaFeign, times(1)).transferencia(cuentaOrigen, cuentaDestino, monto);
        verify(transaccionRepository, times(1)).save(any(Transaccion.class)); // Aquí se ignora el `id`
        verify(transaccionMapper, times(1)).convertirTransaccionResponse(any(Transaccion.class));
    }


}
