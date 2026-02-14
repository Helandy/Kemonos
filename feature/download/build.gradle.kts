plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.download"
}
dependencies {
    implementation(libs.bundles.serialization.json)

    implementation(project(":common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:preferences"))
    implementation(project(":storage-api"))
    implementation(project(":feature:download-api"))
}
