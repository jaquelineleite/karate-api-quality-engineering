package br.com.jaqueline.payment.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
}
