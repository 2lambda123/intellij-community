plugins {
    kotlin("js")
}

repositories {
    { { kts_kotlin_plugin_repositories } }
}

dependencies {
    implementation(kotlin("stdlib"))
}

kotlin {
    js {
        binaries.executable()
        browser()
    }
}
