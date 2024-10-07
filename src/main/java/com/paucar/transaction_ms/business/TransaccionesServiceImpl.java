package com.paucar.transaction_ms.business;

import com.paucar.transaction_ms.business.mapper.TransaccionMapper;
import com.paucar.transaction_ms.client.CuentaFeign;
import com.paucar.transaction_ms.client.dto.CuentaDTO;
import com.paucar.transaction_ms.exceptions.TransferenciaException;
import com.paucar.transaction_ms.model.TransactionResponse;
import com.paucar.transaction_ms.model.dto.ApiResponse;
import com.paucar.transaction_ms.model.entity.Transaccion;
import com.paucar.transaction_ms.repository.TransaccionRepository;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransaccionesServiceImpl implements TransaccionesService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;
    private final CuentaFeign cuentaFeign;

    public TransaccionesServiceImpl(TransaccionRepository transaccionRepository,
                                    TransaccionMapper transaccionMapper,
                                    CuentaFeign cuentaFeign) {
        this.transaccionRepository = transaccionRepository;
        this.transaccionMapper = transaccionMapper;
        this.cuentaFeign = cuentaFeign;
    }

    @Override
    public List<TransactionResponse> obtenerHistorial() {
        return this.transaccionRepository.findAll().stream()
                .map(transaccionMapper::convertirTransaccionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse registrarDeposito(String cuentaDestino, Double monto) {
        try {

            Transaccion transaccion = crearTransaccion("DEPOSITO", monto, null, cuentaDestino);

            ResponseEntity<ApiResponse<CuentaDTO>> respuesta = cuentaFeign.depositar(
                    cuentaDestino,
                    monto
            );

            return procesarOperacion(
                    respuesta.getBody(),
                    "No se pudo realizar el depósito.",
                    transaccion
            );

        } catch (FeignException e) {
            String mensajeError = extraerMensajeError(e);
            throw new TransferenciaException("Error en el depósito: " + mensajeError);
        }
    }

    @Override
    public TransactionResponse registrarRetiro(String cuentaOrigen, Double monto) {
        try {

            Transaccion transaccion = crearTransaccion("RETIRO", monto, cuentaOrigen, null);

            ResponseEntity<ApiResponse<CuentaDTO>> respuesta = cuentaFeign.retirar(
                    cuentaOrigen,
                    monto
            );

            return procesarOperacion(
                    respuesta.getBody(),
                    "No se pudo realizar el retiro.",
                    transaccion
            );

        } catch (FeignException e) {
            String mensajeError = extraerMensajeError(e);
            throw new TransferenciaException("Error en el retiro: " + mensajeError);
        }
    }

    @Override
    public TransactionResponse registrarTransferencia(String cuentaOrigen, String cuentaDestino, Double monto) {
        try {
            Transaccion transaccion = crearTransaccion("TRANSFERENCIA", monto, cuentaOrigen, cuentaDestino);

            ResponseEntity<ApiResponse<Boolean>> respuesta = cuentaFeign.transferencia(
                    cuentaOrigen,
                    cuentaDestino,
                    monto
            );

            return procesarOperacion(
                    respuesta.getBody(),
                    "No se pudo realizar la transferencia.",
                    transaccion
            );

        } catch (FeignException e) {
            String mensajeError = extraerMensajeError(e);
            throw new TransferenciaException(mensajeError);
        }
    }


    private Transaccion crearTransaccion(String tipo, Double monto, String cuentaOrigen, String cuentaDestino) {
        return Transaccion.builder()
                .tipo(tipo)
                .monto(monto)
                .fecha(LocalDate.now())
                .cuentaOrigen(cuentaOrigen)
                .cuentaDestino(cuentaDestino)
                .build();
    }

    private TransactionResponse procesarOperacion(ApiResponse<?> respuesta, String mensajeError, Transaccion transaccion) {
        if (respuesta == null || respuesta.getDatos() == null) {
            throw new TransferenciaException(mensajeError);
        }
        return   this.transaccionMapper.convertirTransaccionResponse(this.transaccionRepository.save(transaccion));
    }


    private String extraerMensajeError(FeignException e) {
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
