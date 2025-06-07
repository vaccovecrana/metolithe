plugins { id("io.vacco.oss.gitflow") version "1.5.4" apply(false) }

subprojects {
  apply(plugin = "io.vacco.oss.gitflow")

  group = "io.vacco.metolithe"
  version = "2.48.0"

  configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addClasspathHell() }
  configure<io.vacco.cphell.ChPluginExtension> { resourceExclusions.add("module-info.class") }
}
