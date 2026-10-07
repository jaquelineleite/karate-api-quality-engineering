package br.com.jaqueline.booking.controller;

import br.com.jaqueline.booking.domain.Booking;
import br.com.jaqueline.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger log =
            LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> create(
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId,
            @RequestBody Booking booking) {

        String effectiveCorrelationId =
                correlationId == null || correlationId.isBlank()
                        ? UUID.randomUUID().toString()
                        : correlationId;

        booking.setCorrelationId(effectiveCorrelationId);

        log.info(
                "Creating booking correlationId={} customerName={}",
                effectiveCorrelationId,
                booking.getCustomerName()
        );

        Booking created = bookingService.create(booking);

        log.info(
                "Booking created bookingId={} correlationId={} status={}",
                created.getId(),
                effectiveCorrelationId,
                created.getStatus()
        );

        return ResponseEntity
                .created(URI.create("/bookings/" + created.getId()))
                .header("X-Correlation-ID", effectiveCorrelationId)
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> findById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Correlation-ID", required = false)
            String correlationId) {

        log.info(
                "Finding booking bookingId={} correlationId={}",
                id,
                correlationId
        );

        return bookingService.findById(id)
                .map(booking -> ResponseEntity.ok()
                        .header(
                                "X-Correlation-ID",
                                booking.getCorrelationId()
                        )
                        .body(booking))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
