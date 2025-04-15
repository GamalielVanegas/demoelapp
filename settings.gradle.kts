pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    // Usamos versiones directas en lugar del catalogo libs
    val agpVersion = "8.3.2"
    val kotlinVersion = "2.0.0"
    val googleServicesVersion = "4.4.2"

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application" -> useModule("com.android.tools.build:gradle:$agpVersion")
                "org.jetbrains.kotlin.android" -> useVersion(kotlinVersion)
                "com.google.gms.google-services" -> useVersion(googleServicesVersion)
            }
        }
    }
}

dependencyResolutionManagement {
    // Suprimimos el warning de API inestable
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DemoleApp"
include(":app")