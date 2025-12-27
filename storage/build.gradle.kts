plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemono.posts"
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

    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.serialization.json)
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    implementation(project(":storage-api"))
    implementation(project(":core-domain"))
    implementation(project(":core"))
    implementation(project(":common"))

    implementation(project(":core-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:posts-api"))
}