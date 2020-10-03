plugins { id("io.vacco.common-build") version "0.5.3" }

subprojects {
  apply(plugin = "io.vacco.common-build")

  group = "io.vacco.metolithe"
  version = "2.0.1"

  configure<io.vacco.common.CbPluginProfileExtension> {
    addJ8Spec()
    addPmd()
    addSpotBugs()
    addClasspathHell()
    setPublishingUrlTransform { repo -> "${repo.url}/${rootProject.name}" }
    sharedLibrary()
  }

  configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}
