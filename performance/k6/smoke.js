import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 1,
  duration: '10s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<1000'],
  },
};

export default function () {
  const response = http.get(
    'https://restful-booker.herokuapp.com/booking'
  );

  check(response, {
    'status deve ser 200': (r) => r.status === 200,
    'resposta deve possuir conteúdo': (r) => r.body.length > 0,
  });

  sleep(1);
}
