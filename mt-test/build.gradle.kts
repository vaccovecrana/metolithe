plugins { jacoco }

tasks.withType<Test> {
  this.testLogging {
    this.showStandardStreams = true
  }
}

tasks.withType<JacocoReport> {
  sourceSets(
      project(":mt-core").sourceSets.main.get(),
      project(":mt-codegen-liquibase").sourceSets.main.get()
  )
}

dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-core"))
  implementation(Libs.joox)
  implementation(Libs.fluentJdbc)

  implementation(project(":mt-codegen-liquibase"))
  implementation(Libs.fastClasspathScanner)
  implementation(Libs.liquibase) {
    exclude("ch.qos.logback", "logback-classic")
    exclude("org.slf4j", "slf4j-api")
  }

  implementation(Libs.j8Spec)
  implementation(Libs.shax)
  implementation(Libs.slf4jApi)
  implementation(Libs.h2)
}