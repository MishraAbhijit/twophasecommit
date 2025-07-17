# 🧾 Distributed Transaction Demo with Two-Phase Commit (2PC)

This repository demonstrates a distributed transaction system using the **Two-Phase Commit (2PC)** protocol with Spring Boot microservices. The system ensures **atomicity** and **consistency** across services like **Inventory**, **Payment**, and **Order** during a single transaction.

---

## 🧩 Modules / Microservices

- **Transaction Service** — Transaction Coordinator (TC)
- **Inventory Service** — Validates and reserves product quantity
- **Payment Service** — Holds or deducts user payment
- **Order Service** — Creates or validates order

Each service exposes `POST` endpoints for:
- `/prepare`
- `/commit`
- `/rollback`

---

## 🚦 What is Two-Phase Commit (2PC)?

The **2PC protocol** ensures that a distributed transaction is either **fully committed** or **fully rolled back** across all participating services.

### Phase 1 — Prepare

- Transaction Coordinator sends `/prepare` requests to all participants.
- Each participant:
    - Validates the transaction.
    - Reserves required resources (e.g., funds, stock).
    - Replies with success or failure.

### Phase 2 — Commit or Rollback

- If **all participants succeed**, TC sends `/commit` requests.
- If **any participant fails**, TC sends `/rollback` requests to all.

---

## 💡 Real-World Analogy: Online Purchase with UPI/Card

1. **Inventory** reserves items.
2. **Payment** gateway redirects user to confirm payment.
3. **Order** service validates cart and customer info.
4. If all `prepare` actions succeed:
    - `commit` is issued → payment is captured, items are deducted, order is placed.
5. If any `prepare` fails or user cancels payment:
    - `rollback` is issued → release inventory, release payment hold.

---

## ✅ Advantages

- Guarantees **strong consistency**.
- Prevents **partial transactions**.
- Straightforward to implement for small systems.

---

## ❌ Drawbacks

| Issue | Explanation |
|-------|-------------|
| ⚠️ Blocking | Participants wait indefinitely if coordinator crashes |
| 🔒 Resource Locking | Locks held during prepare can degrade performance |
| 💥 TC Crash Risk | Without logging, recovery is difficult |
| 🧱 Not Scalable | Not ideal for large-scale, real-time systems |
| 🕒 Timeout Complexity | Participants must implement timeout-based rollback |

---

## 🛠️ Failure Scenarios & Handling

### 1. 🔄 Prepare Phase Failure
- If any service returns error during `/prepare`, TC issues `/rollback` to all.
- **Handled By**: `TransactionServiceImpl` exception block.

### 2. ❌ TC Crashes After Sending Prepare
- Some services are prepared but never receive `/commit` or `/rollback`.
- **Mitigation**:
    - Use persistent logs (DB or file-based).
    - On restart, TC queries participants or uses retry queue.
    - Participants may implement a **timeout** to self-rollback.

### 3. 🚫 Commit Sent, But Service is Down
- Commit sent, but participant is unreachable.
- **Mitigation**:
    - TC retries.
    - Participants must ensure **idempotent commit** logic.

### 4. ⚠️ Partial Commit
- Can occur due to inconsistent state or lack of idempotency.
- **Mitigation**: Use transaction IDs and logs to detect inconsistencies.

---

## 💳 Payment Service Behavior

In a real-world UPI/Card payment:

1. **Prepare Phase**:
    - Call PSP or gateway to **authorize payment** (UPI intent or card auth).
    - Set a TTL (e.g., 10 min hold).

2. **Commit Phase**:
    - **Capture funds** from hold.
    - Complete payment transaction.

3. **Rollback Phase**:
    - **Void** the authorization (cancel payment intent).

> Note: UPI doesn’t formally support intent-capture model yet. Most PSPs require direct payment confirmation.

---
## 🚀 Improvements To Make
    1. Persist transaction state (e.g., in DB)
    2. Participant timeout auto-rollback
    3. Retry logic with exponential backoff
    4. Idempotency for /commit and /rollback

## 📂 TransactionServiceImpl – Coordinator Logic

This class performs the following steps:

```java
// Phase 1: Prepare
POST /inventory/prepare
POST /payment/prepare
POST /order/prepare

// Phase 2: Commit if all succeeded
POST /inventory/commit
POST /payment/commit
POST /order/commit

// Else: Rollback all
POST /inventory/rollback
POST /payment/rollback
POST /order/rollback