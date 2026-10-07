import http from "k6/http";
import { check } from "k6";

export const options = {
  stages: [
    { duration: "10s", target: 50 },
    { duration: "15s", target: 100 },
    { duration: "15s", target: 200 },
    { duration: "15s", target: 300 },
    { duration: "10s", target: 0 }
  ],

  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<2000", "p(99)<3000"]
  }
};

export default function () {
  const correlationId =
    "k6-" + __VU + "-" + __ITER + "-" + Date.now();

  const payload = JSON.stringify({
    customerName: "K6 Load Test",
    totalPrice: 850.00
  });

  const response = http.post(
    "http://localhost:8081/bookings",
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        "X-Correlation-ID": correlationId
      }
    }
  );

  check(response, {
    "status 201": (r) => r.status === 201,
    "booking confirmado": (r) =>
      r.status === 201 && r.json("status") === "CONFIRMED",
    "correlation id preservado": (r) =>
      r.headers["X-Correlation-Id"] === correlationId
  });
}
