# Spring Integration Research Lab (Grade Edition)

## 1\. 프로젝트 개요 (Project Overview)

이 프로젝트는 **Spring Boot 3**와 **Spring Integration 6**를 기반으로 엔터프라이즈 통합 패턴(EIP)을 깊이 있게 연구하기 위한 학습용 애플리케이션입니다.
단순한 "Hello World"를 넘어, 실제 상용 환경에서 발생할 수 있는 복잡한 메시징 흐름을 시뮬레이션합니다.

**목표:**

  * Spring Integration Java DSL (Domain Specific Language) 완벽 숙지.
  * 다양한 Message Channel 타입(Direct, Queue, Pub/Sub, Executor)의 차이점 이해.
  * 주요 Endpoint(Transformer, Router, Filter, Splitter, Aggregator) 실습.
  * 외부 시스템 연동(File, JDBC, HTTP) 및 에러 처리 전략 수립.

**대상 독자:**

  * 이 문서는 AI (Copilot, ChatGPT, Claude 등)에게 프로젝트 스캐폴딩 및 상세 구현을 지시하기 위한 **Requirements Specification**입니다.

## 2\. 기술 스택 (Tech Stack)

  * **Language:** Java 17+ (Record, Text Blocks, Pattern Matching 활용)
  * **Framework:** Spring Boot 3.x
  * **Integration:** Spring Integration 6.x (Core, File, JDBC, HTTP, JMX)
  * **Build Tool:** **Gradle (Kotlin DSL recommended)**
  * **Testing:** JUnit 5, AssertJ, Awaitility, Spring Integration Test Support
  * **Database:** H2 (In-memory mode for JDBC Adapter simulation)
  * **Utils:** Lombok, Jackson

## 3\. 프로젝트 구조 (Directory Structure)

AI는 아래 패키지 구조를 엄격히 준수하여 모듈화된 코드를 생성해야 합니다.

```text
com.example.integrationlab
├── config              # IntegrationFlow Bean 및 Global Channel 설정
├── domain              # DTO, Record, Entity
├── scenarios           # 시나리오별 패키지 분리 (각 시나리오가 독립적이어야 함)
│   ├── basic           # Scenario 1: 기초 (Gateway, ServiceActivator)
│   ├── routing         # Scenario 2: 라우팅 & 필터링
│   ├── aggregation     # Scenario 3: 분할 및 집계 (Parallel Processing)
│   ├── adapter         # Scenario 4: 외부 시스템 어댑터 (File, JDBC)
│   └── resiliency      # Scenario 5: 에러 처리 및 재시도 (Error Handling)
├── service             # 비즈니스 로직 (Mock Services)
└── IntegrationLabApplication.java
```

## 4\. 핵심 시나리오 (Core Research Scenarios)

AI는 아래 5가지 시나리오를 각각 별도의 `IntegrationFlow` 빈으로 구현해야 합니다.

### Scenario 1: 기초 파이프라인과 데이터 변환 (Fundamentals)

> **학습 목표:** Gateway, Transformer, Service Activator, Logging

1.  **Messaging Gateway:** `String processText(String input)` 인터페이스를 통해 진입.
2.  **Transformer:** 입력된 문자열을 모두 대문자로 변환하고, `payload`에 타임스탬프 헤더를 추가한 JSON 객체로 변환.
3.  **WireTap:** 처리 중간 과정을 `loggingChannel`로 비동기 전송하여 로그 출력.
4.  **Service Activator:** 최종 결과를 반환.

### Scenario 2: 동적 라우팅과 필터링 (Flow Control)

> **학습 목표:** Content Based Router, Filter, Recipient List Router

1.  **Input:** `Order` 객체 (속성: `amount`, `type`, `isVip`).
2.  **Filter:** `amount < 1000` 인 주문은 **Discard Channel**로 이동시키고 흐름 종료.
3.  **Router (Header Value Router):** 주문 타입(`type`)에 따라 다른 채널로 분기.
      * `ELECTRONICS` -\> 전자제품 처리 채널
      * `BOOKS` -\> 도서 처리 채널
4.  **Recipient List Router:** 만약 `isVip`가 true라면, 위 처리와 별개로 `vipNotificationChannel`로 복제 메시지 전송 (Pub/Sub 효과).

### Scenario 3: 대량 데이터 분산 처리 (Splitter & Aggregator)

> **학습 목표:** Splitter, Aggregator, ExecutorChannel (Async), Barrier

1.  **Splitter:** `List<String> sentences`를 받아 개별 문장(`String`)으로 쪼갬.
2.  **Channel:** `ExecutorChannel`을 사용하여 멀티 스레드로 병렬 처리 시작.
3.  **Service Activator (Async):** 각 문장의 단어 수를 세는 작업 수행 (Random `Thread.sleep`으로 지연 모사).
4.  **Aggregator:** 분산 처리된 결과(`wordCount`)들을 모아 최종적으로 `TotalWordCountReport` 객체 하나로 합침.
      * **Release Strategy:** 모든 조각이 도착하거나 5초가 지나면(Timeout) 부분 결과만으로 반환.

### Scenario 4: 이기종 시스템 통합 (Adapters - File & JDBC)

> **학습 목표:** Inbound/Outbound Channel Adapter, Poller

1.  **File Inbound Adapter:** 로컬 디렉토리(`input/files`)를 1초마다 폴링하여 `.txt` 파일 감지.
2.  **Transformer:** 파일 내용을 읽어 `String`으로 변환.
3.  **JDBC Outbound Adapter:** H2 데이터베이스의 `AUDIT_LOG` 테이블에 파일명과 내용을 Insert.
4.  **File Outbound Adapter (Success):** 처리가 완료된 파일은 `processed/` 폴더로 이동(Rename).

### Scenario 5: 결함 허용 및 모니터링 (Resiliency & Error Handling)

> **학습 목표:** Global Error Channel, RequestHandlerRetryAdvice, Circuit Breaker

1.  **Unstable Service:** 50% 확률로 예외를 던지는 서비스를 호출.
2.  **Retry Advice:** 예외 발생 시 최대 3회 재시도 (Backoff 설정 포함).
3.  **Error Handling:** 3회 실패 시 `RecoveryCallback`이 동작하여 `ErrorMessage`를 `parkingLotChannel`로 전송.
4.  **Control Bus:** 특정 메시지를 보내 시스템의 특정 어댑터를 런타임에 중지/재시작(`@managedOperation`) 시키는 관리 기능 구현.

## 5\. Gradle 빌드 설정 (Build Configuration)

AI는 아래 의존성을 `build.gradle.kts`에 반드시 포함해야 합니다.

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.integration:spring-integration-file")
    implementation("org.springframework.integration:spring-integration-jdbc")
    implementation("org.springframework.integration:spring-integration-http")
    implementation("com.h2database:h2")
    testImplementation("org.springframework.integration:spring-integration-test")
    testImplementation("org.awaitility:awaitility") // 비동기 테스트 필수
}
```

## 6\. 테스트 전략 (JUnit 5 & Mocking)

단순 단위 테스트가 아닌, **통합 흐름(Integration Flow) 자체를 검증**해야 합니다.

1.  **MockIntegration:** 실제 File I/O나 DB 없이 채널 흐름만 검증할 때 사용.
2.  **ArgumentCaptor:** 메시지가 채널을 통과할 때 Payload의 변형 상태 검증.
3.  **Awaitility:** Scenario 3, 4와 같은 비동기/폴링 흐름에서 결과가 도착할 때까지 대기하며 검증.
      * *Example:* "파일을 넣고 3초 안에 DB에 데이터가 1건 생겨야 한다."

-----

## 7\. AI 프롬프트 가이드 (How to Instruct AI)

이 README를 사용하여 AI에게 코드를 요청할 때는 **"Bottom-Up"** 방식으로 접근하세요.

### Step 1: 프로젝트 세팅

> "이 `README.md`의 **3. 프로젝트 구조**와 **5. Gradle 설정**에 맞춰 Spring Boot 3 프로젝트의 기본 뼈대(Scaffolding)를 만들어줘. `build.gradle.kts` 파일을 먼저 보여줘."

### Step 2: 시나리오 구현 (반복)

> "이제 \*\*Scenario 2 (동적 라우팅)\*\*를 구현할 차례야. `IntegrationFlow` Bean 설정 코드와 이를 테스트하기 위한 `JUnit 5` 테스트 코드를 작성해. `MockIntegration`을 적극적으로 활용해서 검증해줘."

### Step 3: 심화 구현

> "**Scenario 5**의 에러 핸들링을 구현해줘. 특히 `RequestHandlerRetryAdvice`를 사용해서 재시도 로직을 적용하고, 실패 시 복구 로직이 작동하는 것을 테스트 코드로 증명해줘."

-----

## 8\. 실행 및 검증 (Execution)

```bash
# 테스트 실행 (모든 시나리오 검증)
./gradlew test

# 앱 실행
./gradlew bootRun
```
