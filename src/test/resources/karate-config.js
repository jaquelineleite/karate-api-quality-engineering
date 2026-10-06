function fn() {
    var env = karate.env || 'dev';

    karate.log('Ambiente de execução:', env);

    var config = {
        env: env,
        baseUrl: 'https://restful-booker.herokuapp.com'
    };

    if (env == 'dev') {
        config.baseUrl = 'https://restful-booker.herokuapp.com';
    }

    karate.configure('connectTimeout', 10000);
    karate.configure('readTimeout', 10000);

    return config;
}
