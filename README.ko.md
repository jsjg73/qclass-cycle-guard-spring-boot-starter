# qclass-cycle-guard-spring-boot-starter

`META-INF/cyclic-qclasses.txt`에 기록된 순환 의존성 Q-class를 **애플리케이션 시작 시점**에 단일 스레드에서 미리 로딩하는 Spring Boot Starter입니다.

## 문제

QueryDSL이 생성하는 Q-class는 다른 Q-class를 static 필드로 참조합니다. 순환 참조가 존재할 때(예: QOrder → QCustomer → QOrder), 멀티스레드 환경에서 클래스 초기화 시 JVM 레벨 데드락이 발생할 수 있으며, 이는 진단이 매우 어렵습니다.

## 해결 방법

이 Starter는 **런타임 시작 시점**에 다음을 수행합니다:

1. 클래스패스에서 `META-INF/cyclic-qclasses.txt` 리소스를 탐색
2. 파일에 기록된 Q-class FQCN을 **단일 스레드(CommandLineRunner)**에서 순차적으로 로딩
3. 멀티스레드가 해당 클래스에 접근하기 전에 초기화를 완료하여 데드락 방지

> `META-INF/cyclic-qclasses.txt`는 [qclass-cycle-guard-plugin](https://github.com/jsjg73/qclass-cycle-guard-plugin)이 빌드 시점에 자동 생성합니다.

## 사용법

### 1. 의존성 추가

**build.gradle**
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.jsjg73:qclass-cycle-guard-spring-boot-starter:v0.1.3'
}
```

**pom.xml**
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.jsjg73</groupId>
    <artifactId>qclass-cycle-guard-spring-boot-starter</artifactId>
    <version>v0.1.3</version>
</dependency>
```

### 2. 별도 설정 불필요

Spring Boot Auto-configuration으로 등록되므로 별도 설정 없이 동작합니다. 애플리케이션 시작 시 아래와 같은 로그가 출력됩니다.

```
INFO - Loaded 3 Q-classes from myapp.jar (12ms): [com.example.QOrder, com.example.QCustomer, ...]
```

## 동작 방식

```
ApplicationContext 초기화 → CommandLineRunner 실행 → META-INF/cyclic-qclasses.txt 탐색 → Q-class 순차 로딩
```

1. **탐색** — 클래스패스 전체에서 `META-INF/cyclic-qclasses.txt`를 검색 (JAR 다중 포함 시 모두 처리)
2. **로딩** — `Class.forName(fqcn, true, classLoader)`로 static 초기화까지 완료
3. **로그** — 로딩된 클래스 수, 소스 JAR, 소요 시간을 INFO 레벨로 출력

## 전체 파이프라인 (플러그인 + Starter)

```
빌드 시점: qclass-cycle-guard-plugin → META-INF/cyclic-qclasses.txt 생성
런타임:    qclass-cycle-guard-spring-boot-starter → Q-class 미리 로딩 → 데드락 방지
```

두 라이브러리를 함께 사용하는 것을 권장합니다.

## 호환성

- Java 17+
- Spring Boot 3.x
- QueryDSL 4.x / 5.x

## 라이선스

[Apache License 2.0](LICENSE)
