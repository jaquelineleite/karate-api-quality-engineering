package br.com.jaqueline.pix.domain;

import java.math.BigDecimal;

public record PixRequest(
        String payerAccount,
        String receiverPixKey,
        BigDecimal amount,
        String description
) {}
