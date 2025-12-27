plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(libs.bundles.navigation3)
    implementation(project(":core-domain"))
}