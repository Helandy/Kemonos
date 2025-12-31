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
    implementation(libs.bundles.serialization.json)

    /** Временно пока не вынесется main в отдельную фичу */
    implementation(libs.bundles.navigation3)
}