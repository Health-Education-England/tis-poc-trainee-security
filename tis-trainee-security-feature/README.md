# TIS Trainee Security Feature

A **standalone, service-agnostic Spring Security library** to enforce **JWT feature-based access
control** in Spring Boot microservices. Provides annotations for method-level authorization.

---

## Features

 * `hasFeature` – Security expression to secure controller or service methods based on **nested JWT
                  feature flags**.
 * `FeatureChecker` – Spring Bean that parses JWT claims and evaluates nested `"enabled"` flags.

---

## Installation

Add the library to your microservice Gradle dependencies:

```gradle
dependencies {
    implementation "uk.nhs.tis.trainee:security-feature:0.0.1"
}
```

---

## Usage

### 1. Enable method security and register the required beans

```java
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

  @Bean
  public FeatureChecker featureChecker() {
    return new FeatureChecker();
  }

  @Bean
  public MethodSecurityExpressionHandler featureMethodSecurityExpressionHandler(
    FeatureChecker featureChecker) {
    return new FeatureMethodSecurityExpressionHandler(featureChecker);
  }
}
```

### 2. Protect controller methods with `hasFeature` security expression

```java
@RestController
@RequestMapping("/ltft")
public class LtftController {

    @PostMapping
    @PreAuthorize("hasFeature('forms.ltft')")
    public ResponseEntity<String> createLtft(@RequestBody LtftFormDto dto) {
        return ResponseEntity.ok("Created LTFT form!");
    }
}
```

* Checks JWT claim `features.forms.ltft.enabled` at runtime.
* Supports **nested paths** like `details.profile.gmcUpdate.enabled`.

---

## Development

### Build

```bash
./gradlew clean build
```

### Publish locally

```bash
./gradlew publishToMavenLocal
```

You must provide a signing key or disable `signAllPublications()` in the build script.

---

## Compatibility

 * Java 17+ (compatible with Spring Boot services running JDK 17)
 * Spring Boot 3.x
 * Fully **service-agnostic** — does not include any domain-specific checks.

---

## License

[MIT License](LICENSE)
