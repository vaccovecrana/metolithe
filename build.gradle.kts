plugins { id("io.vacco.oss.gitflow") version "1.8.3" apply(false) }

subprojects {
  apply(plugin = "io.vacco.oss.gitflow")
  group = "io.vacco.metolithe"
  version = "3.7.2"
}
