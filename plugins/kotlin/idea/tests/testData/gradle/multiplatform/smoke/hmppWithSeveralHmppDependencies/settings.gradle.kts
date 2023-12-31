pluginManagement {
    repositories {
        { { kts_kotlin_plugin_repositories } }
    }
    plugins {
        kotlin("multiplatform") version "{{kgp_version}}"
        kotlin("android") version "{{kgp_version}}"
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:{{agp_version}}")
            }
        }
    }
}

rootProject.name = "mpp-issue-bootstrap"
include(":p1")
