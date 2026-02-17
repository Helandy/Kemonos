plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.creatorProfile"
}

dependencies {
    implementation(libs.bundles.serialization.json)

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.paging)
    implementation(libs.bundles.coil)

    implementation(project(":core:navigation"))
    implementation(project(":core:model"))

    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))
    implementation(project(":core:deepLink"))
    implementation(project(":core:error"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":storage-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:commonScreen-api"))
}
