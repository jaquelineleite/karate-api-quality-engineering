package br.com.jaqueline.pix.controller;

import br.com.jaqueline.pix.domain.PixRequest;
import br.com.jaqueline.pix.domain.PixResponse;
import br.com.jaqueline.pix.service.PixService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix/transfers")
public class PixController {

    private final PixService pixService;

    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @PostMapping
    public ResponseEntity<PixResponse> create(
            @RequestHeader(value = "X-Idempotency-Key", required = false)
            String idempotencyKey,
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId,
            @RequestBody PixRequest request) {

        PixResponse response = pixService.create(
                idempotencyKey,
                correlationId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PixResponse> findById(
            @PathVariable String transactionId) {

        return ResponseEntity.ok(
                pixService.findById(transactionId)
        );
    }
}
