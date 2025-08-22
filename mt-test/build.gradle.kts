configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addJ8Spec() }

tasks.withType<JacocoReport> {
  sourceSets(
    project(":mt-core").sourceSets.main.get()
  )
}

dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-codegen"))
  implementation("io.vacco.shax:shax:2.0.16.0.4.3")
  implementation("com.h2database:h2:2.1.210")
  implementation("org.apache.commons:commons-lang3:3.18.0")
}
