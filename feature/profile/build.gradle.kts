plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.profile"

    defaultConfig {
        val vName = libs.versions.appVersionName.get()
        buildConfigField("String", "VERSION_NAME", "\"$vName\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.coil)

    implementation(libs.bundles.androidx.credentials)
    implementation(libs.bundles.paging)

    implementation(project(":core:navigation"))
    implementation(project(":core:deepLink"))
    implementation(project(":core:model"))

    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))

    implementation(project(":storage-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:download-api"))
}
