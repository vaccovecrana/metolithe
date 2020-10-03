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

  implementation("io.vacco.shax:shax:1.7.30.0.0.6")
  implementation("com.h2database:h2:1.4.197")
  implementation("org.apache.commons:commons-lang3:3.11")
}
