pluginManagement {
  repositories {
    jcenter(); gradlePluginPortal()
    maven { name = "VaccoOss"; setUrl("https://dl.bintray.com/vaccovecrana/vacco-oss") }
  }
}

include("mt-annotations", "mt-core", "mt-codegen", "mt-test")