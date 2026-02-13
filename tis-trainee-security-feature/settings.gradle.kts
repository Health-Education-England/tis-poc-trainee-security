rootProject.name = "tis-trainee-security-feature"

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }

  versionCatalogs {
    create("libs") {
      from("uk.nhs.tis.trainee:version-catalog:0.0.8")
    }
  }
}
