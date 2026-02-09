plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "su.afk.kemonos.posts"
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

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.navigation3)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.paging)

    implementation(project(":common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:domain"))
    implementation(project(":core:preferences"))
    implementation(project(":core:network"))

    implementation(project(":feature:posts-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":storage-api"))
}