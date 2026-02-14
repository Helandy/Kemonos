plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.videoPlayer"
}
dependencies {
    implementation(libs.bundles.serialization.json)

    implementation(libs.bundles.media3)

    implementation(project(":core:navigation"))
    implementation(project(":common"))
    implementation(project(":core:model"))
}
