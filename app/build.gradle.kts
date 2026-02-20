plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "su.afk.kemonos"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "su.afk.kemonos"
        testApplicationId = "su.afk.kemonos.test"

        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()

        versionName = libs.versions.appVersionName.get()
        versionCode = libs.versions.appVersionCode.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        release {
            signingConfig = signingConfigs.getByName("debug")

            isMinifyEnabled = true
            isShrinkResources = true

            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("boolean", "LOG_HTTP", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    debugImplementation(libs.leakcanary.android)

    implementation(libs.bundles.hilt)
    ksp(libs.dagger.hilt.compiler)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.core)
    debugImplementation(libs.bundles.compose.debug)

    implementation(libs.bundles.navigation3)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.lifecycle)

    implementation(project(":core:navigation"))

    implementation(project(":core:model"))
    implementation(project(":core:auth"))
    implementation(project(":core:network"))
    implementation(project(":core:deepLink"))
    implementation(project(":core:preferences"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))
    implementation(project(":core:error"))

    implementation(project(":feature:commonScreen-api"))
    implementation(project(":feature:commonScreen"))

    implementation(project(":storage-api"))
    implementation(project(":storage"))

    implementation(project(":feature:creators-api"))
    implementation(project(":feature:creators"))

    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:creatorProfile"))

    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorPost"))

    implementation(project(":feature:posts-api"))
    implementation(project(":feature:posts"))

    implementation(project(":feature:profile-api"))
    implementation(project(":feature:profile"))

    implementation(project(":feature:appUpdate-api"))
    implementation(project(":feature:appUpdate"))

    implementation(project(":feature:download-api"))
    implementation(project(":feature:download"))

    implementation(project(":feature:main-api"))
    implementation(project(":feature:main"))
}

val releaseApkName = "kemonos-${libs.versions.appVersionName.get()}-release.apk"

tasks.register<Sync>("exportReleaseApkForGithub") {
    val releaseDir = layout.buildDirectory.dir("outputs/apk/release")
    from(releaseDir.map { it.file(releaseApkName) })
    from(releaseDir.map { it.file("app-release.apk") })
    into(layout.buildDirectory.dir("outputs/apk/githubRelease"))
    rename("app-release.apk", releaseApkName)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("assembleRelease")
}

tasks.configureEach {
    if (name == "assembleRelease") {
        finalizedBy("exportReleaseApkForGithub")
    }
}
