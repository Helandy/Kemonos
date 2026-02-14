plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(libs.bundles.navigation3)
    implementation(libs.bundles.serialization.json)
    implementation(project(":core:model"))

}