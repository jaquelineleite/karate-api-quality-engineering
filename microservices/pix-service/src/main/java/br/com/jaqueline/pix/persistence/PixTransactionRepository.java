package br.com.jaqueline.pix.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PixTransactionRepository
        extends JpaRepository<PixTransactionEntity, String> {

    Optional<PixTransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
