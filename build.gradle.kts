plugins { id("io.vacco.oss.gitflow") version "0.9.8" apply(false) }

subprojects {
  apply(plugin = "io.vacco.oss.gitflow")

  group = "io.vacco.metolithe"
  version = "2.5.0"

  configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addClasspathHell() }
}
