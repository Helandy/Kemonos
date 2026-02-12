plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")

    // Feature-specific plugins.
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val hiltCompiler = libs.findLibrary("dagger-hilt-compiler").get()
val hiltBundle = libs.findBundle("hilt").get()
val composeBom = libs.findLibrary("androidx-compose-bom").get()
val composeCoreBundle = libs.findBundle("compose-core").get()
val navigation3Bundle = libs.findBundle("navigation3").get()

fun DependencyHandlerScope.implementation(dep: Any) = add("implementation", dep)
fun DependencyHandlerScope.ksp(dep: Any) = add("ksp", dep)

android {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()

    defaultConfig {
        minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.findVersion("jvmVersion").get().requiredVersion)
        targetCompatibility = JavaVersion.toVersion(libs.findVersion("jvmVersion").get().requiredVersion)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Hilt + generated code.
    ksp(hiltCompiler)
    implementation(hiltBundle)

    // Compose + navigation baseline for feature screens.
    implementation(platform(composeBom))
    implementation(composeCoreBundle)
    implementation(navigation3Bundle)
}
