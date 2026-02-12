plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.appUpdate"
}

dependencies {
    implementation(libs.bundles.retrofit)

    implementation(project(":feature:appUpdate-api"))
}
