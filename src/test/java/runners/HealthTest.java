package runners;

import com.intuit.karate.junit5.Karate;

public class HealthTest {

    @Karate.Test
    Karate testHealth() {
        return Karate.run("classpath:features/health/health.feature");
    }
}
