package runners;

import com.intuit.karate.junit5.Karate;

public class MicroservicesRunner {

    @Karate.Test
    Karate testMicroservices() {
        return Karate.run("classpath:features/microservices")
                .tags("@microservices");
    }
}
