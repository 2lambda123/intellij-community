plugins {
    id("java")
}

group = "org.example"

repositories {
    mavenCentral()
}
java {
    setTargetCompatibility(JavaVersion.VERSION_11)
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}