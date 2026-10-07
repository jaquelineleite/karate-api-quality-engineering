function fn() {
    var env = karate.env || 'dev';

    karate.log('Ambiente de execução:', env);

    var defaultBaseUrl = 'https://restful-booker.herokuapp.com';

    var baseUrl =
        karate.properties['baseUrl'] ||
        java.lang.System.getenv('BASE_URL') ||
        defaultBaseUrl;

    var bookingServiceUrl = karate.properties['bookingServiceUrl'] || java.lang.System.getenv('BOOKING_SERVICE_URL') || 'http://localhost:8081';

    var paymentServiceUrl = karate.properties['paymentServiceUrl'] || java.lang.System.getenv('PAYMENT_SERVICE_URL') || 'http://localhost:8082';

    var authUsername =
        karate.properties['authUsername'] ||
        java.lang.System.getenv('AUTH_USERNAME') ||
        'admin';

    var authPassword =
        karate.properties['authPassword'] ||
        java.lang.System.getenv('AUTH_PASSWORD') ||
        'password123';

    var config = {
        env: env,
        baseUrl: baseUrl,
        bookingServiceUrl: bookingServiceUrl,
        paymentServiceUrl: paymentServiceUrl,
        authUsername: authUsername,
        authPassword: authPassword
    };

    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);

    return config;
}
