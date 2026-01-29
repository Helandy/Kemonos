plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "su.afk.kemonos.deepLink"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildFeatures {
        compose = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.bundles.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.bundles.navigation3)

    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:creatorPost-api"))
}