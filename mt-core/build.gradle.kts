configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { sharedLibrary(true, false) }

val api by configurations

dependencies {
  api(project(":mt-annotations"))
  api("org.codejargon:fluentjdbc:1.8.6")
}
