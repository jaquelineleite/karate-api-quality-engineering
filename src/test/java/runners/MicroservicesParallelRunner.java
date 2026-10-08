package runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MicroservicesParallelRunner {

    @Test
    void executarCenariosEmParalelo() {
        Results results = Runner.path("classpath:features/microservices")
                .tags("@microservices")
                .parallel(3);

        assertEquals(3, results.getScenariosTotal(),
                "Devem ser executados três cenários");

        assertEquals(0, results.getFailCount(),
                results.getErrorMessages());
    }
}
