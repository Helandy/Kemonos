plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "su.afk.kemonos.common"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(libs.bundles.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.bundles.lifecycle)
    implementation(libs.material3)

    implementation(libs.bundles.retrofit)

    implementation(libs.androidx.core.ktx)

    implementation(libs.bundles.navigation3)

    implementation(libs.bundles.paging)

    implementation(libs.multiplatform.markdown.renderer.m3)

    implementation(libs.bundles.compose.core)
    implementation(libs.bundles.androidx.base)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.media3)
    implementation(libs.bundles.datastore)

    implementation(project(":navigation"))
    implementation(project(":core-domain"))
    implementation(project(":core-api"))
    implementation(project(":storage-api"))
    implementation(project(":feature:appUpdate-api"))
    implementation(project(":feature:common:commonScreen-api"))
}