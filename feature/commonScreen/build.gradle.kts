plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.commonScreen"
}
dependencies {
    implementation(libs.bundles.coil)

    implementation(project(":common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))

    implementation(project(":feature:commonScreen-api"))
}
