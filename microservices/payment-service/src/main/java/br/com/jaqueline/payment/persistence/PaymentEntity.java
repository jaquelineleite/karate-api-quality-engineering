package br.com.jaqueline.payment.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_payments_idempotency_key",
                columnNames = "idempotency_key"
        )
})
public class PaymentEntity {

    @Id
    @Column(name = "payment_id", nullable = false, length = 36)
    private String paymentId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "correlation_id")
    private String correlationId;

    protected PaymentEntity() {
    }

    public PaymentEntity(
            String paymentId,
            String idempotencyKey,
            Long bookingId,
            BigDecimal amount,
            String status,
            String correlationId) {

        this.paymentId = paymentId;
        this.idempotencyKey = idempotencyKey;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
        this.correlationId = correlationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
