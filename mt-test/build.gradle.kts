tasks.withType<JacocoReport> {
  sourceSets(
      project(":mt-core").sourceSets.main.get(),
      project(":mt-codegen").sourceSets.main.get()
  )
}

dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-core"))
  implementation(Libs.fluentJdbc)

  implementation(project(":mt-codegen"))
  implementation(Libs.joox)
  implementation(Libs.liquibase) {
    exclude("ch.qos.logback", "logback-classic")
    exclude("org.slf4j", "slf4j-api")
  }

  implementation(Libs.j8Spec)
  implementation(Libs.shax)
  implementation(Libs.slf4jApi)
  implementation(Libs.h2)
  implementation(Libs.lang3)
}
