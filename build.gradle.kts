buildscript {
  repositories { maven { name = "VaccoOss"; setUrl("https://dl.bintray.com/vaccovecrana/vacco-oss") } }
  dependencies { classpath("io.vacco.common:common-build:0.1.0") }
}

allprojects {
  apply(from = project.buildscript.classLoader.getResource("io/vacco/common/java-library.gradle.kts").toURI())

  group = "io.vacco.metolithe"
  version = "2.0.0"

  configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
