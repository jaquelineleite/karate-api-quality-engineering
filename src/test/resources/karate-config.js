function fn() {
    var env = karate.env || 'dev';

    karate.log('Ambiente de execução:', env);

    var defaultBaseUrl = 'https://restful-booker.herokuapp.com';

    var baseUrl =
        karate.properties['baseUrl'] ||
        java.lang.System.getenv('BASE_URL') ||
        defaultBaseUrl;

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
        authUsername: authUsername,
        authPassword: authPassword
    };

    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);

    return config;
}
