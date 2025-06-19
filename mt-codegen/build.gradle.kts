configure<io.vacco.oss.gitflow.GsPluginProfileExtension> { sharedLibrary(true, false) }

val api by configurations

dependencies {
  api(project(":mt-core"))
  api("io.vacco.oriax:oriax:0.1.1")
  api("io.marioslab.basis:template:[1,)")
}
