plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = "com.chimericdream"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()

        // Required to resolve a `local(...)` IDE; it is not part of defaultRepositories().
        localPlatformArtifacts()
    }
}

dependencies {
    intellijPlatform {
        val localIde = providers.gradleProperty("localIdePath").orNull

        if (localIde.isNullOrBlank()) {
            intellijIdeaUltimate("2026.2.1")
        } else {
            local(localIde)
        }
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            // 262 == 2026.2. No upper bound: the two APIs this plugin touches
            // (projectViewNodeDecorator, BulkFileListener) are long-stable.
            sinceBuild = "262"
            untilBuild = provider { null }
        }
    }
}

// The IDE runs on JBR 25, so 25 is the highest bytecode level it can load. No Java toolchain is
// declared on purpose -- this compiles with whatever JDK runs Gradle (25+) and targets 25.
tasks.withType<JavaCompile>().configureEach {
    options.release = 25
    options.encoding = "UTF-8"
}

// `./gradlew runIde -PsandboxProject=<path>` boots a throwaway IDE with this plugin installed and
// opens that project straight away, which is the only way to actually look at the decoration.
tasks.named<JavaExec>("runIde") {
    providers.gradleProperty("sandboxProject").orNull?.let { args(it) }
}
