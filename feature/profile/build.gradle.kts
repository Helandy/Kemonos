plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "su.afk.kemonos.profile"
    compileSdk = libs.versions.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    defaultConfig {
        val vName = libs.versions.appVersionName.get()

        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
    }
}
dependencies {
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.bundles.hilt)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)

    implementation(libs.bundles.navigation3)

    implementation(libs.bundles.retrofit)

    implementation(project(":common"))
    implementation(project(":navigation"))

    implementation(project(":core-domain"))
    implementation(project(":core"))
    implementation(project(":core-api"))
    implementation(project(":storage-api"))

    implementation(project(":feature:profile-api"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:creatorPost-api"))
}