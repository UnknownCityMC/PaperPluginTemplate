import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.run)
}

group = "de.unknowncity"
version = "0.1.0"

// REPLACE PaperTemplatePlugin with the plugin name!
val mainClass = "${group}.${rootProject.name.lowercase()}.${project.name}Plugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.unknowncity.de/snapshots")
    maven("https://repo.unknowncity.de/releases")
    maven("https://jitpack.io")
    maven("https://repo.xenondevs.xyz/releases")
    maven {
        url = uri("https://repo.unknowncity.de/private")
        credentials (PasswordCredentials::class) {
            username = System.getenv("MVN_REPO_USERNAME")
            password = System.getenv("MVN_REPO_PASSWORD")
        }
    }
}

dependencies {
    implementation(libs.invui)
    compileOnly(libs.astralib)
    compileOnly(libs.paper)
}

bukkit {
    name = "UC-${project.name}"
    version = "${rootProject.version}"

    // REPLACE with fitting description
    description = "Super cool sample plugin"

    author = "UnknownCity"

    main = mainClass

    foliaSupported = false

    apiVersion = libs.versions.minecraft.get()

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    // Add dependency plugins
    softDepend = listOf()
    depend = listOf("AstraLib")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        fun relocateDependency(from : String) = relocate(from, "$shadeBasePath$from")

        relocateDependency("xyz.xenondevs.invui")
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())

        jvmArgs("-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            // ADD plugins needed for testing
            // E.g: url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
        }
    }
}