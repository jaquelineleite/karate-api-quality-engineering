package br.com.jaqueline.payment.domain;

import java.math.BigDecimal;

public record PaymentRequest(
        Long bookingId,
        BigDecimal amount
) {
}
