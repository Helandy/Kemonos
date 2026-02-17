plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.posts"
}
dependencies {
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.paging)

    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:preferences"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":core:error"))

    implementation(project(":feature:posts-api"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":storage-api"))
}
