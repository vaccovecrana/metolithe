configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addJ8Spec() }

tasks.withType<JacocoReport> {
  sourceSets(
      project(":mt-core").sourceSets.main.get(),
      project(":mt-codegen").sourceSets.main.get()
  )
}

dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-core"))
  implementation(project(":mt-codegen"))

  implementation("io.vacco.shax:shax:2.0.6.0.1.0")
  implementation("com.h2database:h2:2.1.214")
  implementation("org.apache.commons:commons-lang3:3.12.0")
}
