plugins { id("io.vacco.oss.gitflow") version "0.9.8" apply(false) }

subprojects {
  apply(plugin = "io.vacco.oss.gitflow")

  group = "io.vacco.metolithe"
  version = "2.9.4"

  configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { addClasspathHell() }
  configure<io.vacco.cphell.ChPluginExtension> { resourceExclusions.add("module-info.class") }
}
