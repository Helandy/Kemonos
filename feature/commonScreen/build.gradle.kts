plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.commonScreen"
}
dependencies {
    implementation(libs.bundles.serialization.json)
    implementation(libs.bundles.coil)

    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))

    implementation(project(":feature:commonScreen-api"))
    implementation(project(":feature:download-api"))
}
