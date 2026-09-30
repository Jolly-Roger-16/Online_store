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
        maven {
            url = uri("http://nexus.ngknn.local:8081/repository/maven-public/")
            isAllowInsecureProtocol = true
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://nexus.ngknn.local:8081/repository/maven-public/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "OnlineStore"
include(":app")
