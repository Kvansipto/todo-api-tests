package infra

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.net.URI
import java.util.*

object TestEnvironment {

    private val config = Properties().also {
        javaClass.getResourceAsStream("/testconfig.properties")?.use(it::load)
            ?: error("testconfig.properties not found")
    }

    private val imagePath = config.getProperty("docker.image.path")
    private val imageUrl = config.getProperty("docker.image.download")
    private val imageName = config.getProperty("docker.image.name")
    private lateinit var container: GenericContainer<*>

    fun startDockerApp(exposedPort: Int = 4242): Int {
        if (!isImageLoaded()) {
            val imageFile = File(imagePath)
            if (!imageFile.exists()) {
                try {
                    URI(imageUrl).toURL().openStream().use { input ->
                        imageFile.outputStream().use { output -> input.copyTo(output) }
                    }
                } catch (e: Exception) {
                    error("Failed to download Docker image: ${e.message}")
                }
            }
            ProcessBuilder("docker", "load", "-i", imagePath)
                .inheritIO()
                .start()
                .waitFor()
        }

        container = GenericContainer(DockerImageName.parse(imageName))
            .withExposedPorts(exposedPort)
            .withEnv("VERBOSE", "1")
        container.start()
        return container.getMappedPort(exposedPort)
    }

    fun stop() {
        container.stop()
    }

    private fun isImageLoaded(): Boolean {
        val process = ProcessBuilder("docker", "images", "-q", imageName)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        return output.isNotEmpty()
    }
}
