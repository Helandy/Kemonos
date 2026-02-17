plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "su.afk.kemonos.ui"
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
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.bundles.hilt)

    implementation(libs.okhttp)

    implementation(libs.bundles.paging)

    implementation(libs.multiplatform.markdown.renderer.m3)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.datastore)

    implementation(libs.bundles.coil)
    implementation(libs.bundles.translate)
    implementation(libs.bundles.jsoup)

    debugImplementation(libs.bundles.compose.debug)

    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:error"))
    implementation(project(":core:utils"))

    implementation(project(":storage-api"))
    implementation(project(":feature:appUpdate-api"))
    implementation(project(":feature:commonScreen-api"))
}
