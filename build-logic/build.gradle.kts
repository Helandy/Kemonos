plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    // Build-logic is an included build with its own classpath.
    // These dependencies provide plugin implementations used by precompiled script plugins.
    implementation(libs.findLibrary("gradle-plugin-android").get())
    implementation(libs.findLibrary("gradle-plugin-kotlin").get())
    implementation(libs.findLibrary("gradle-plugin-kotlin-compose").get())
    implementation(libs.findLibrary("gradle-plugin-hilt").get())
    implementation(libs.findLibrary("gradle-plugin-ksp").get())
}
