configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { sharedLibrary(true, false) }

val api by configurations

dependencies {
  api(project(":mt-annotations"))
  api(project(":mt-core"))
  api("io.vacco.oriax:oriax:0.1.1")
  api("org.jooq:joox:1.6.2")
  api("io.marioslab.basis:template:1.7")
  api("org.liquibase:liquibase-core:4.4.3") {
    exclude("ch.qos.logback", "logback-classic")
    exclude("org.slf4j", "slf4j-api")
  }
}
