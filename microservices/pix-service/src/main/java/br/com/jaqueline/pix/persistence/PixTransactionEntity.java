package br.com.jaqueline.pix.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
    name = "pix_transactions",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_pix_idempotency_key",
        columnNames = "idempotency_key"
    )
)
public class PixTransactionEntity {

    @Id
    @Column(name = "transaction_id", nullable = false, length = 36)
    private String transactionId;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "payer_account", nullable = false)
    private String payerAccount;

    @Column(name = "receiver_pix_key", nullable = false)
    private String receiverPixKey;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "correlation_id")
    private String correlationId;

    protected PixTransactionEntity() {
    }

    public PixTransactionEntity(
            String transactionId,
            String idempotencyKey,
            String payerAccount,
            String receiverPixKey,
            BigDecimal amount,
            String description,
            String status,
            String correlationId) {

        this.transactionId = transactionId;
        this.idempotencyKey = idempotencyKey;
        this.payerAccount = payerAccount;
        this.receiverPixKey = receiverPixKey;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.correlationId = correlationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public String getReceiverPixKey() {
        return receiverPixKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
