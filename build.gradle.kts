import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PublishTask

buildscript {
    repositories {
        jcenter()
        maven { setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service") }
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("jacoco")
    id("org.jetbrains.intellij") version "0.4.8"
}

group = "com.github.shiraji"
version = System.getProperty("VERSION") ?: "0.0.1"

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
    maxHeapSize = "3g"
}

jacoco {
    toolVersion = "0.8.2"
}

val jacocoTestReport by tasks.existing(JacocoReport::class) {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

repositories {
    mavenCentral()
    jcenter()
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IU-2018.1.4"

    setPlugins(
            // https://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/plugin_compatibility.html
            "Kotlin",
            "Pythonid:2018.1.181.5087.50", // https://plugins.jetbrains.com/plugin/631-python
            "org.jetbrains.plugins.ruby:2018.1.20180515", // https://plugins.jetbrains.com/plugin/1293-ruby
            "yaml",
            "org.jetbrains.plugins.go:181.5087.39.204", // https://plugins.jetbrains.com/plugin/9568-go
            "com.jetbrains.php:181.5087.11", // https://plugins.jetbrains.com/plugin/6610-php
            "JavaScriptLanguage",
            "markdown",
            "Groovy",
            "org.intellij.scala:2018.1.4", // https://plugins.jetbrains.com/plugin/1347-scala
            "org.rust.lang:0.2.0.2107-181" // https://plugins.jetbrains.com/plugin/8182-rust
    )
    updateSinceUntilBuild = false
}


val patchPluginXml: PatchPluginXmlTask by tasks

patchPluginXml {
    changeNotes(
        """
        <p>1.0.3</p>
        <ul>
          <li>Add support for more text files (txt, MD, ... files) #32 Thanks @olivmindee for reporting!</li>
        </ul>
        <p>Older version changes are listed on <a href="https://github.com/shiraji/yet-another-emoji-support/blob/master/CHANGELOG.md">CHANGELOG.md</a></p>
        """.trimIndent()
    )
}

val publishPlugin: PublishTask by tasks

publishPlugin {
    token(System.getenv("HUB_TOKEN"))
    channels(System.getProperty("CHANNELS") ?: "beta")
}

dependencies {
    val kotlinVersion: String by project
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    testImplementation("io.mockk:mockk:1.8.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

configurations {
    create("ktlint")

    dependencies {
        add("ktlint", "com.github.shyiko:ktlint:0.30.0")
    }
}

tasks.register("ktlintCheck", JavaExec::class) {
    description = "Check Kotlin code style."
    classpath = configurations["ktlint"]
    main = "com.github.shyiko.ktlint.Main"
    args("src/**/*.kt")
}

tasks.register("ktlintFormat", JavaExec::class) {
    description = "Fix Kotlin code style deviations."
    classpath = configurations["ktlint"]
    main = "com.github.shyiko.ktlint.Main"
    args("-F", "src/**/*.kt")
}

tasks.register("resolveDependencies") {
    doLast {
        project.rootProject.allprojects.forEach {subProject ->
            subProject.buildscript.configurations.forEach {configuration ->
                if (configuration.isCanBeResolved) {
                    configuration.resolve()
                }
            }
            subProject.configurations.forEach {configuration ->
                if (configuration.isCanBeResolved) {
                    configuration.resolve()
                }
            }
        }
    }
}

inline operator fun <T : Task> T.invoke(a: T.() -> Unit): T = apply(a)
