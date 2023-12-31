pluginManagement {
    repositories {
        repositories {
            { { kts_kotlin_plugin_repositories } }
        }
    }
    plugins {
        kotlin("multiplatform") version "{{kgp_version}}"
        kotlin("jvm") version "{{kgp_version}}"
        kotlin("android") version "{{kgp_version}}"
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:{{agp_version}}")
            }
        }
    }

    dependencyResolutionManagement {
        repositories {
            { { kts_kotlin_plugin_repositories } }
        }
    }
}

include(":androidApp")
include(":jvmLibrary")
include(":multiplatformAndroidApp")
include(":multiplatformAndroidJvmIosLibrary")
include(":multiplatformAndroidJvmIosLibrary2")
include(":multiplatformAndroidLibrary")
include(":multiplatformJvmLibrary")
