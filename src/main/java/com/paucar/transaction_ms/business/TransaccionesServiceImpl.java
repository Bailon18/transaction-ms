package com.paucar.transaction_ms.business;

import com.paucar.transaction_ms.business.mapper.TransaccionMapper;
import com.paucar.transaction_ms.client.CuentaFeign;
import com.paucar.transaction_ms.client.dto.CuentaDTO;
import com.paucar.transaction_ms.error.ErrorHandler;
import com.paucar.transaction_ms.factory.TransaccionFactory;
import com.paucar.transaction_ms.exceptions.TransferenciaException;
import com.paucar.transaction_ms.model.TransactionResponse;
import com.paucar.transaction_ms.model.dto.ApiResponse;
import com.paucar.transaction_ms.model.entity.Transaccion;
import com.paucar.transaction_ms.repository.TransaccionRepository;
import com.paucar.transaction_ms.validation.OperacionValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransaccionesServiceImpl implements TransaccionesService {

    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;
    private final CuentaFeign cuentaFeign;


    @Override
    public List<TransactionResponse> obtenerHistorial() {
        return this.transaccionRepository.findAll().stream()
                .map(transaccionMapper::convertirTransaccionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse registrarDeposito(String cuentaDestino, Double monto) {
        try {
            Transaccion transaccion = TransaccionFactory.crearTransaccion("DEPOSITO", monto, null, cuentaDestino);

            ResponseEntity<ApiResponse<CuentaDTO>> respuesta = cuentaFeign.depositar(cuentaDestino, monto);

            OperacionValidator.validarOperacion(respuesta.getBody(), "No se pudo realizar el depósito.");
            return transaccionMapper.convertirTransaccionResponse(transaccionRepository.save(transaccion));

        } catch (FeignException e) {
            throw new TransferenciaException("Error en el depósito: " + ErrorHandler.extraerMensajeError(e));
        }
    }

    @Override
    public TransactionResponse registrarRetiro(String cuentaOrigen, Double monto) {
        try {
            Transaccion transaccion = TransaccionFactory.crearTransaccion("RETIRO", monto, cuentaOrigen, null);

            ResponseEntity<ApiResponse<CuentaDTO>> respuesta = cuentaFeign.retirar(cuentaOrigen, monto);

            OperacionValidator.validarOperacion(respuesta.getBody(), "No se pudo realizar el retiro.");
            return transaccionMapper.convertirTransaccionResponse(transaccionRepository.save(transaccion));

        } catch (FeignException e) {
            throw new TransferenciaException("Error en el retiro: " + ErrorHandler.extraerMensajeError(e));
        }
    }

    @Override
    public TransactionResponse registrarTransferencia(String cuentaOrigen, String cuentaDestino, Double monto) {
        try {
            Transaccion transaccion = TransaccionFactory.crearTransaccion("TRANSFERENCIA", monto, cuentaOrigen, cuentaDestino);

            ResponseEntity<ApiResponse<Boolean>> respuesta = cuentaFeign.transferencia(cuentaOrigen, cuentaDestino, monto);

            OperacionValidator.validarOperacion(respuesta.getBody(), "No se pudo realizar la transferencia.");
            return transaccionMapper.convertirTransaccionResponse(transaccionRepository.save(transaccion));

        } catch (FeignException e) {
            throw new TransferenciaException(ErrorHandler.extraerMensajeError(e));
        }
    }
}
