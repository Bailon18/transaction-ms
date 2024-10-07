package com.paucar.transaction_ms.business.mapper;

import com.paucar.transaction_ms.model.TransactionResponse;
import com.paucar.transaction_ms.model.entity.Transaccion;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {
    TransactionResponse convertirTransaccionResponse(Transaccion transaccion);
}
