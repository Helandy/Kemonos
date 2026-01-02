plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "su.afk.kemonos.auth"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.bundles.hilt)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.serialization.json)
    implementation(libs.bundles.datastore)
    implementation(libs.androidx.security.crypto)

    implementation(project(":core:domain"))
    implementation(project(":feature:profile-api"))
}