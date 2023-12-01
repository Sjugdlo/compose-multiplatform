plugins {
    kotlin("multiplatform") apply false
    id("com.android.library") apply false
}

allprojects {
    repositories {
        mavenLocal() // mavenLocal should be the first to get the correct version of skiko during a local build.
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        // TODO: delete when we have all libs in mavenCentral
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

subprojects {
    version = findProperty("deploy.version") ?: property("compose.version")!!

    plugins.withId("java") {
        configureIfExists<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11

            withJavadocJar()
            withSourcesJar()
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
        kotlinOptions.jvmTarget = "11"
    }

    plugins.withId("maven-publish") {
        configureIfExists<PublishingExtension> {
            repositories {
                maven {
                    name = "ComposeRepo"
                    setUrl(System.getenv("COMPOSE_REPO_URL"))
                    credentials {
                        username = System.getenv("COMPOSE_REPO_USERNAME")
                        password = System.getenv("COMPOSE_REPO_KEY")
                    }
                }
            }
        }
    }
}