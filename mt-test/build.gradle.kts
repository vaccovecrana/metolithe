configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addJ8Spec() }

tasks.withType<JacocoReport> {
  sourceSets(
    project(":mt-core").sourceSets.main.get(),
    project(":mt-codegen").sourceSets.main.get()
  )
  afterEvaluate {
    classDirectories.setFrom(
      files(classDirectories.files.map {
        fileTree(it) {
          exclude("io/vacco/mt/test/**") // Replace with your package
        }
      })
    )
  }
}

dependencies {
  implementation(project(":mt-annotations"))
  implementation(project(":mt-core"))
  implementation(project(":mt-codegen"))

  implementation("io.vacco.shax:shax:2.0.16.0.4.3")
  implementation("com.h2database:h2:2.2.224")
  implementation("org.apache.commons:commons-lang3:3.14.0")
}
