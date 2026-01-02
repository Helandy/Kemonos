plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.storage"
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

    implementation(libs.bundles.serialization.json)

    ksp(libs.androidx.room.compiler)
    implementation(libs.bundles.room)

    implementation(project(":storage-api"))

    implementation(project(":core:domain"))
    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))
    implementation(project(":common"))

    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:posts-api"))
}