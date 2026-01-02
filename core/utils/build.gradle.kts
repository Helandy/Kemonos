plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(libs.bundles.coroutines)

    implementation(project(":core-domain"))
}