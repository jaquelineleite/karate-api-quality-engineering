package runners;

import com.intuit.karate.junit5.Karate;

public class NegativeTest {

    @Karate.Test
    Karate testNegativeScenarios() {
        return Karate.run("classpath:features/negative/booking-negative.feature");
    }
}
