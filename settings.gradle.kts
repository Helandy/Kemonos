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

include(":common")
include(":navigation")
include(":core-domain")

include(":feature:common:commonScreen-api")
include(":feature:common:commonScreen")

include(":core:auth")
include(":core:network")
include(":core:preferences")
include(":core:utils")

include(":storage-api")
include(":storage")

include(":feature:creators-api")
include(":feature:creators")

include(":feature:creatorProfile-api")
include(":feature:creatorProfile")

include(":feature:creatorPost-api")
include(":feature:creatorPost")

include(":feature:posts-api")
include(":feature:posts")

include(":feature:profile-api")
include(":feature:profile")

include(":feature:appUpdate-api")
include(":feature:appUpdate")

include(":feature:download-api")
include(":feature:download")

include(":feature:main-api")
include(":feature:main")

include(":feature:videoPlayer")