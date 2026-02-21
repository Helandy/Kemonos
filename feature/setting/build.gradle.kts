plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.setting"

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
    implementation(libs.bundles.coil)

    implementation(project(":core:navigation"))
    implementation(project(":core:deepLink"))
    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))

    implementation(project(":storage-api"))
    implementation(project(":feature:setting-api"))
    implementation(project(":feature:profile"))
}
