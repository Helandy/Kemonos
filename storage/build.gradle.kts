plugins {
    id("kemonos.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "su.afk.kemonos.storage"
}

dependencies {
    implementation(libs.bundles.serialization.json)

    ksp(libs.androidx.room.compiler)
    implementation(libs.bundles.room)

    implementation(libs.bundles.datastore)

    implementation(project(":storage-api"))
    implementation(project(":core:model"))

    implementation(project(":core:preferences"))
    implementation(project(":core:utils"))
    implementation(project(":common"))

    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:posts-api"))
}
