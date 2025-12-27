plugins {
    kotlin("jvm")
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.retrofit)
}