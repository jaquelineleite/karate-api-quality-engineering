package support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PixDatabaseHelper {

    public static Map<String, Object> findByIdempotencyKey(String key) throws Exception {

        String url = System.getenv().getOrDefault(
                "PIX_DB_URL",
                "jdbc:postgresql://localhost:5435/bookingdb"
        );

        String user = System.getenv().getOrDefault("PIX_DB_USER", "qa_user");
        String password = System.getenv().getOrDefault("PIX_DB_PASSWORD", "qa_password");

        String sql = """
                SELECT transaction_id, description, status, amount
                FROM pix_transactions
                WHERE idempotency_key = ?
                """;

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, key);

            try (ResultSet result = statement.executeQuery()) {

                Map<String, Object> data = new HashMap<>();

                if (!result.next()) {
                    data.put("count", 0);
                    return data;
                }

                data.put("transactionId", result.getString("transaction_id"));
                data.put("description", result.getString("description"));
                data.put("status", result.getString("status"));
                data.put("amount", result.getBigDecimal("amount"));

                int count = 1;

                while (result.next()) {
                    count++;
                }

                data.put("count", count);

                return data;
            }
        }
    }
}
