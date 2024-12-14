import de.nilsdruyen.gradle.ftp.UploadExtension
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.nilsdruyen.gradle-ftp-upload-plugin") version "0.4.2"
}

group = "de.unknowncity"
version = "0.1.0"

// REPLACE PaperTemplatePlugin with the plugin name!
val mainClass = "${group}.${rootProject.name.lowercase()}.PaperTemplatePlugin"
val shadeBasePath = "${group}.${rootProject.name.lowercase()}.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.unknowncity.de/snapshots")
    maven("https://repo.unknowncity.de/releases")
    maven("https://jitpack.io")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven {
        url = uri("https://repo.unknowncity.de/private")
        credentials (PasswordCredentials::class) {
            username = System.getenv("MVN_REPO_USERNAME")
            password = System.getenv("MVN_REPO_PASSWORD")
        }
    }
}

dependencies {

    // Command library
    bukkitLibrary("org.incendo", "cloud-paper", "2.0.0-beta.9")
    bukkitLibrary("org.incendo", "cloud-minecraft-extras", "2.0.0-beta.9")

    // Database
    bukkitLibrary("de.chojo.sadu", "sadu-queries", "2.2.1")
    bukkitLibrary("de.chojo.sadu", "sadu-mariadb", "2.2.1")
    bukkitLibrary("de.chojo.sadu", "sadu-datasource", "2.2.1")
    bukkitLibrary("de.chojo.sadu", "sadu-updater", "2.2.1")


    // User interface library
    // implementation("xyz.xenondevs.invui", "invui", "1.37")

    // Economy system
    //compileOnly("su.nightexpress.coinsengine", "CoinsEngine", "2.3.3")

    // Placeholder api
    //compileOnly("me.clip", "placeholderapi", "2.11.6")

    compileOnly("de.unknowncity.astralib", "astralib-paper-api", "0.5.0-SNAPSHOT")

    compileOnly("io.papermc.paper", "paper-api", "1.21.1-R0.1-SNAPSHOT")
}

bukkit {
    // REPLACE PaperTemplatePlugin with the plugin name!
    name = "UC-PaperTemplatePlugin"
    version = "${rootProject.version}"

    // REPLACE with fitting description
    description = "Super cool sample plugin"

    author = "UnknownCity"

    main = mainClass

    foliaSupported = false

    apiVersion = "1.21"

    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD

    // Add dependency plugins
    softDepend = listOf()
    depend = listOf("AstraLib")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
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
        minecraftVersion("1.21.1")

        jvmArgs("-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            // ADD plugins needed for testing
            // E.g: url("https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar")
        }
    }

    configure<UploadExtension> {
        host = System.getenv("UC_FTP_HOST").orEmpty()
        port = (System.getenv("UC_FTP_PORT") ?: "0").toInt()
        username = System.getenv("UC_FTP_THAUPT_USR").orEmpty()
        password = System.getenv("UC_FTP_THAUPT_PWD").orEmpty()
        sourceDir = "${rootProject.layout.buildDirectory}/libs"
        targetDir = "/plugins/"
    }

    register("deployToTestServer") {
        dependsOn(uploadFilesToFtp)
        println("Restarting server...")
        val request = HttpRequest.newBuilder(URI.create("https://panel.unknowncity.de/api/client/servers/f3eb8390/power"))
            .header("Accept", "applcation/json")
            .header("Content-Type", "applcation/json")
            .header("Authorization", "Bearer ${System.getenv("UC_PANEL_API_TOKEN")}")
            .POST(HttpRequest.BodyPublishers.ofString("{ \"signal\": \"restart\"}"))
            .build()

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    register<Copy>("copyToServer") {
        val path = System.getenv("SERVER_DIR")
        if (path.toString().isEmpty()) {
            println("No SERVER_DIR env variable set")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }
}