plugins {
    id("kemonos.android.feature")
}

android {
    namespace = "su.afk.kemonos.main"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:auth"))
    implementation(project(":core:preferences"))
    implementation(project(":core:network"))
    implementation(project(":core:utils"))
    implementation(project(":core:error"))

    implementation(project(":feature:main-api"))
    implementation(project(":feature:posts-api"))
    implementation(project(":feature:appUpdate-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":storage-api"))
}
