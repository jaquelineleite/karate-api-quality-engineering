package runners;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class PixConcurrencyTest {

    private static final int TOTAL_REQUESTS = 10;

    @Test
    void pix015_devePreservarIdempotenciaComRequisicoesConcorrentes() throws Exception {

        String idempotencyKey = "pix-concorrencia-auto-" + UUID.randomUUID();

        String payload = """
                {
                  "payerAccount": "12345-6",
                  "receiverPixKey": "concorrencia-auto@exemplo.com",
                  "amount": 500.00,
                  "description": "PIX-015 concorrencia automatizada"
                }
                """;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_REQUESTS);
        CountDownLatch inicio = new CountDownLatch(1);

        List<Future<HttpResponse<String>>> futures = new ArrayList<>();

        try {
            for (int i = 1; i <= TOTAL_REQUESTS; i++) {

                final int numero = i;

                Callable<HttpResponse<String>> tarefa = () -> {

                    inicio.await();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8083/pix/transfers"))
                            .timeout(Duration.ofSeconds(30))
                            .header("Content-Type", "application/json")
                            .header("X-Idempotency-Key", idempotencyKey)
                            .header("X-Correlation-ID", "pix-auto-" + numero)
                            .POST(HttpRequest.BodyPublishers.ofString(payload))
                            .build();

                    return client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );
                };

                futures.add(executor.submit(tarefa));
            }

            inicio.countDown();

            Pattern transactionIdPattern =
                    Pattern.compile("\"transactionId\"\\s*:\\s*\"([^\"]+)\"");

            String transactionIdOriginal = null;

            for (int i = 0; i < futures.size(); i++) {

                HttpResponse<String> response = futures.get(i).get();

                System.out.println(
                        "PIX-015 | Requisicao " + (i + 1)
                        + " | HTTP " + response.statusCode()
                );

                assertEquals(
                        201,
                        response.statusCode(),
                        "Requisicao " + (i + 1) + ": " + response.body()
                );

                Matcher matcher =
                        transactionIdPattern.matcher(response.body());

                assertTrue(
                        matcher.find(),
                        "Resposta sem transactionId: " + response.body()
                );

                String transactionId = matcher.group(1);

                if (transactionIdOriginal == null) {
                    transactionIdOriginal = transactionId;
                } else {
                    assertEquals(
                            transactionIdOriginal,
                            transactionId,
                            "Foram encontrados transactionIds diferentes"
                    );
                }
            }


            // PIX-015: validacao automatizada da persistencia
            try (Connection connection = openDatabaseConnection()) {

                String sql = """
                        SELECT transaction_id, status
                        FROM pix_transactions
                        WHERE idempotency_key = ?
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, idempotencyKey);

                    try (ResultSet result = statement.executeQuery()) {
                        assertTrue(result.next(),
                                "Nenhuma transacao encontrada no PostgreSQL");

                        assertEquals(
                                transactionIdOriginal,
                                result.getString("transaction_id"),
                                "Transaction ID diferente do banco"
                        );

                        assertEquals(
                                "PENDING",
                                result.getString("status")
                        );

                        assertFalse(result.next(),
                                "Mais de uma transacao encontrada para a mesma chave");
                    }
                }
            }

            System.out.println("PIX-015 | PostgreSQL: 1 transacao confirmada");

            System.out.println("PIX-015 | Chave: " + idempotencyKey);
            System.out.println("PIX-015 | Transaction ID: " + transactionIdOriginal);
            System.out.println("PIX-015 | Dez requisicoes validadas");

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void pix016_deveRejeitarPayloadDiferenteComMesmaChaveConcorrente() throws Exception {

        String key = "pix-conflito-auto-" + UUID.randomUUID();

        String[] payloads = {
                """
                {
                  "payerAccount": "12345-6",
                  "receiverPixKey": "destino-a@exemplo.com",
                  "amount": 100.00,
                  "description": "PIX-016 concorrencia"
                }
                """,
                """
                {
                  "payerAccount": "12345-6",
                  "receiverPixKey": "destino-b@exemplo.com",
                  "amount": 200.00,
                  "description": "PIX-016 concorrencia"
                }
                """
        };

        HttpClient client = HttpClient.newHttpClient();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);

        try {
            List<Future<HttpResponse<String>>> futures = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                final int indice = i;

                futures.add(executor.submit(() -> {
                    inicio.await();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8083/pix/transfers"))
                            .timeout(Duration.ofSeconds(30))
                            .header("Content-Type", "application/json")
                            .header("X-Idempotency-Key", key)
                            .header("X-Correlation-ID", "pix-conflito-auto-" + indice)
                            .POST(HttpRequest.BodyPublishers.ofString(payloads[indice]))
                            .build();

                    return client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );
                }));
            }

            inicio.countDown();

            HttpResponse<String> primeira = futures.get(0).get();
            HttpResponse<String> segunda = futures.get(1).get();

            System.out.println("PIX-016 | Requisicao A: HTTP " + primeira.statusCode());
            System.out.println("PIX-016 | Requisicao B: HTTP " + segunda.statusCode());

            assertTrue(
                    (primeira.statusCode() == 201 && segunda.statusCode() == 409)
                    || (primeira.statusCode() == 409 && segunda.statusCode() == 201),
                    "Esperado HTTP 201 e 409. Obtido: "
                            + primeira.statusCode() + " e " + segunda.statusCode()
            );

            HttpResponse<String> vencedora =
                    primeira.statusCode() == 201 ? primeira : segunda;

            int indiceVencedora = primeira.statusCode() == 201 ? 0 : 1;

            Matcher matcher = Pattern.compile(
                    "\"transactionId\"\\s*:\\s*\"([^\"]+)\""
            ).matcher(vencedora.body());

            assertTrue(matcher.find(), "Resposta vencedora sem transactionId");

            String transactionId = matcher.group(1);

            try (Connection connection = openDatabaseConnection()) {

                String sql = """
                        SELECT transaction_id, receiver_pix_key, amount,
                               status, correlation_id
                        FROM pix_transactions
                        WHERE idempotency_key = ?
                        """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, key);

                    try (ResultSet result = statement.executeQuery()) {
                        assertTrue(result.next(), "Transacao nao encontrada");

                        assertEquals(transactionId, result.getString("transaction_id"));
                        assertEquals(
                                indiceVencedora == 0
                                        ? "destino-a@exemplo.com"
                                        : "destino-b@exemplo.com",
                                result.getString("receiver_pix_key")
                        );

                        assertEquals(
                                0,
                                result.getBigDecimal("amount").compareTo(
                                        new java.math.BigDecimal(
                                                indiceVencedora == 0 ? "100.00" : "200.00"
                                        )
                                )
                        );

                        assertEquals("PENDING", result.getString("status"));
                        assertEquals(
                                "pix-conflito-auto-" + indiceVencedora,
                                result.getString("correlation_id")
                        );

                        assertFalse(result.next(), "Duplicidade encontrada no PostgreSQL");
                    }
                }
            }

            System.out.println("PIX-016 | PostgreSQL: 1 transacao confirmada");
            System.out.println("PIX-016 | Chave: " + key);
            System.out.println("PIX-016 | Transaction ID: " + transactionId);

        } finally {
            executor.shutdownNow();
        }
    }


    private static Connection openDatabaseConnection() throws Exception {
        String url = System.getenv().getOrDefault(
                "PIX_DB_URL",
                "jdbc:postgresql://localhost:5435/bookingdb"
        );
        String user = System.getenv().getOrDefault("PIX_DB_USER", "qa_user");
        String password = System.getenv().getOrDefault(
                "PIX_DB_PASSWORD",
                "qa_password"
        );

        return DriverManager.getConnection(url, user, password);
    }
}
