package com.paucar.transaction_ms.repository;

import com.paucar.transaction_ms.model.entity.Transaccion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransaccionRepository extends MongoRepository<Transaccion, String> {
}
