allprojects {

  group = "io.vacco.metolithe"
  version = "2.0.0"

  buildscript { repositories { maven { url = uri("https://plugins.gradle.org/m2/") } } }
  repositories { jcenter() }

  apply(plugin = "java")

  configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  tasks.withType<Test> { this.testLogging { this.showStandardStreams = true } }
}
