plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.compose)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(libs.bundles.navigation3)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
}
