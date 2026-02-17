plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.creatorPost"
}

dependencies {
    implementation(libs.bundles.serialization.json)

    implementation(libs.bundles.coil)
    implementation(libs.bundles.retrofit)


    implementation(project(":core:navigation"))
    implementation(project(":core:model"))

    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:preferences"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:error"))

    implementation(project(":feature:creatorPost-api"))
    implementation(project(":storage-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:download-api"))
    implementation(project(":feature:commonScreen-api"))
}
