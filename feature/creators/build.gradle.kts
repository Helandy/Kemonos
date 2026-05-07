plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.creators"
}

dependencies {
    implementation(libs.bundles.serialization.json)
    implementation(libs.bundles.paging)
    implementation(libs.bundles.retrofit)

    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))
    implementation(project(":core:utils"))

    implementation(project(":storage-api"))
    implementation(project(":feature:creators-api"))
    implementation(project(":feature:creatorProfile-api"))
}
