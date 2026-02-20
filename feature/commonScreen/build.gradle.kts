plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.commonScreen"
}
dependencies {
    implementation(libs.bundles.coil)

    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))

    implementation(project(":feature:commonScreen-api"))
    implementation(project(":feature:download-api"))
}
