plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "su.afk.kemonos.download"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
dependencies {
    implementation(libs.bundles.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.navigation3)

    implementation(libs.bundles.retrofit)

    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.bundles.paging)

    implementation(project(":common"))
    implementation(project(":navigation"))
    implementation(project(":core-domain"))
    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":feature:posts-api"))

    implementation(project(":feature:creatorPost-api"))

    implementation(project(":storage-api"))
}