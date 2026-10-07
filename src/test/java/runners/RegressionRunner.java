package runners;

import com.intuit.karate.junit5.Karate;

public class RegressionRunner {

    @Karate.Test
    Karate testRegression() {
        return Karate.run("classpath:features")
                .tags("@regression");
    }
}
