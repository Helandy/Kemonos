plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "su.afk.kemonos.storage.api"
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
    implementation(libs.bundles.paging)
    implementation(project(":core:model"))

    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:posts-api"))
}
