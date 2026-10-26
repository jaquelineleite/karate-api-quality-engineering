package br.com.jaqueline.pix.domain;

import java.math.BigDecimal;

public record PixResponse(
        String transactionId,
        String payerAccount,
        String receiverPixKey,
        BigDecimal amount,
        String status,
        String correlationId
) {}
