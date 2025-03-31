# TODO API Test Suite

A test project for verifying the functionality of a simple TODO application (CRUD + WebSocket).  
The original test assignment is described in [`task.md`](task.md).

---

## Technologies Used

- Kotlin
- Gradle
- JUnit 5
- Testcontainers
- OkHttp (HTTP + WebSocket)
- kotlinx.serialization
- Hamcrest

---

## Requirements

- Docker
- JDK 21

---

## How to Run

1. **Clone this project**
```bash
git clone https://github.com/Kvansipto/todo-api-tests.git
cd todo-api-tests
```
2. ***Run the tests***
```bash
./gradlew test
```

The application under test (TODO app in Docker) will be automatically launched in a container using Testcontainers.  
There’s no need to run the Docker image manually — the test suite handles it.
