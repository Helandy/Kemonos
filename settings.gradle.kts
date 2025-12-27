pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kemonos"
include(":app")

include(":navigation")
include(":common")
include(":core-domain")

include(":core")
include(":core-api")

include(":storage")
include(":storage-api")

include(":feature:creators")
include(":feature:creators-api")

include(":feature:creatorProfile")
include(":feature:creatorProfile-api")

include(":feature:creatorPost")
include(":feature:creatorPost-api")

include(":feature:posts")
include(":feature:posts-api")

include(":feature:profile")
include(":feature:profile-api")

include(":feature:videoPlayer")

include(":feature:appUpdate")
include(":feature:appUpdate-api")

include(":feature:common:commonScreen")
include(":feature:common:commonScreen-api")