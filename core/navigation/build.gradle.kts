plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "su.afk.kemonos.navigation"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.bundles.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.navigation3)

    implementation(project(":feature:main-api"))
    implementation(project(":feature:posts-api"))
    implementation(project(":feature:creators-api"))
    implementation(project(":feature:profile-api"))
}
