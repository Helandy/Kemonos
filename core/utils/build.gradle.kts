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
    implementation(libs.androidx.paging.common)
    implementation(project(":core:model"))
}
