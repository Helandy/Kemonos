plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.creatorProfile"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.bundles.hilt)

    implementation(libs.bundles.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.navigation3)

    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.paging)
    implementation(libs.bundles.coil)

    implementation(project(":common"))
    implementation(project(":navigation"))

    implementation(project(":core:domain"))
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":storage-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:videoPlayer"))
}