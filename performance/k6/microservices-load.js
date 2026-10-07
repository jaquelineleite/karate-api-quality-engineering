import http from "k6/http";
import { check } from "k6";

export const options = {
  stages: [
    { duration: "10s", target: 10 },
    { duration: "20s", target: 25 },
    { duration: "20s", target: 50 },
    { duration: "10s", target: 0 }
  ],

  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<1500", "p(99)<2500"]
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
