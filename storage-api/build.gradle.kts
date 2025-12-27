plugins {
    kotlin("jvm")
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmVersion.get().toInt()))
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":feature:creatorPost-api"))
    implementation(project(":feature:creatorProfile-api"))
    implementation(project(":feature:profile-api"))
    implementation(project(":feature:posts-api"))
}