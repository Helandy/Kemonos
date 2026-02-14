plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.creators"
}

dependencies {
    implementation(libs.bundles.paging)
    implementation(libs.bundles.retrofit)

    implementation(project(":common"))
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:network"))
    implementation(project(":storage-api"))
    implementation(project(":feature:creators-api"))

    implementation(project(":feature:creatorProfile-api"))
}
