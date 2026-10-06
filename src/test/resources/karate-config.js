function fn() {
    var env = karate.env || 'dev';

    karate.log('Ambiente de execução:', env);

    var defaultBaseUrl = 'https://restful-booker.herokuapp.com';

    var baseUrl =
        karate.properties['baseUrl'] ||
        java.lang.System.getenv('BASE_URL') ||
        defaultBaseUrl;

    var config = {
        env: env,
        baseUrl: baseUrl
    };

    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);

    return config;
}
