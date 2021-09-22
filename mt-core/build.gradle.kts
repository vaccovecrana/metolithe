configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { sharedLibrary(true, false) }

val api by configurations

dependencies {
  api(project(":mt-annotations"))
  api("io.vacco.oruzka:oruzka:0.1.5.1")
  api("org.codejargon:fluentjdbc:1.8.6")
}
