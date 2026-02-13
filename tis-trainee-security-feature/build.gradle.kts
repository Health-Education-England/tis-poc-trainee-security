import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  `java-library`
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)

  // Code Quality
  checkstyle
  jacoco
  alias(libs.plugins.sonarqube)

  // Publishing
  id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "uk.nhs.tis.trainee"
version = "0.0.1"

dependencies {
  implementation("org.springframework.security:spring-security-oauth2-resource-server")
  implementation("org.springframework.security:spring-security-oauth2-jose")
}

checkstyle {
  config =
    resources.text.fromArchiveEntry(configurations.checkstyle.get().first(), "google_checks.xml")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
    vendor.set(JvmVendorSpec.ADOPTIUM)
  }
}

mavenPublishing {
  configure(
    JavaLibrary(
      javadocJar = JavadocJar.Javadoc(),
      sourcesJar = SourcesJar.Sources()
    )
  )
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  coordinates(artifactId = "security-feature")

  pom {
    name = "TIS Trainee Security Feature"
    description =
      "A security library for TIS Trainee services, to enforce feature flag-based access control."
    url = "https://github.com/Health-Education-England"

    developers {
      developer {
        name = "NHS England"
      }
    }

    licenses {
      license {
        name = "MIT"
        url = "https://opensource.org/license/mit"
      }
    }

    scm {
      url = "https://github.com/Health-Education-England/tis-trainee-security-feature"
      connection.set("scm:git:git://github.com/Health-Education-England/tis-trainee-security-feature.git")
      developerConnection.set("scm:git:ssh://git@github.com/Health-Education-England/tis-trainee-security-feature.git")
    }
  }
}

sonarqube {
  properties {
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.login", System.getenv("SONAR_TOKEN"))
    property("sonar.organization", "health-education-england")
    property("sonar.projectKey", "Health-Education-England_tis-trainee-security-feature")
    property(
      "sonar.java.checkstyle.reportPaths",
      "build/reports/checkstyle/main.xml,build/reports/checkstyle/test.xml"
    )
  }
}

testing {
  suites {
    configureEach {
      if (this is JvmTestSuite) {
        useJUnitJupiter()
        dependencies {
          implementation(project())
        }
      }
    }

    val test by getting(JvmTestSuite::class) {
      dependencies {
        implementation("org.hamcrest:hamcrest")
        implementation("org.mockito:mockito-core")
      }
    }

    register<JvmTestSuite>("integrationTest") {
      dependencies {
        implementation("org.springframework.boot:spring-boot-starter-test")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.security:spring-security-config")
        implementation("org.springframework.security:spring-security-test")
      }

      // Include implementation dependencies.
      val integrationTestImplementation by configurations.getting {
        extendsFrom(configurations.implementation.get())
      }
    }
  }
}

publishing {
  publications.withType<MavenPublication>().configureEach {
    versionMapping {
      usage("java-api") {
        fromResolutionOf("runtimeClasspath")
      }
      usage("java-runtime") {
        fromResolutionResult()
      }
    }
  }
}

tasks.named<BootJar>("bootJar") {
  enabled = false
}

tasks.named("check") {
  dependsOn(testing.suites.named("integrationTest"))
}

tasks.jacocoTestReport {
  reports {
    html.required.set(true)
    xml.required.set(true)
  }
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}
