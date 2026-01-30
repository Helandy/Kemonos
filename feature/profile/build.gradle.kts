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

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        val vName = libs.versions.appVersionName.get()
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
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
    implementation(libs.bundles.coil)

    implementation(libs.bundles.androidx.credentials)

    implementation(project(":common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:deepLink"))

    implementation(project(":core:domain"))
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))
    implementation(project(":storage-api"))

    implementation(project(":feature:profile-api"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:creatorPost-api"))
}