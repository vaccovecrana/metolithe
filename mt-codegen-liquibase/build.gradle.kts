dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-core"))
  implementation(Libs.oriax)
  implementation(Libs.joox)
  implementation(Libs.slf4jApi)
  implementation(Libs.liquibase) {
    exclude("ch.qos.logback", "logback-classic")
    exclude("org.slf4j", "slf4j-api")
  }
}
