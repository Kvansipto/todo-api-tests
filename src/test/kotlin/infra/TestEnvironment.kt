package infra

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.net.URI

object TestEnvironment {

    private lateinit var container: GenericContainer<*>

    fun startDockerApp(exposedPort: Int = TestConfig.exposedPort): Int {
        if (!isImageLoaded()) {
            val imageFile = File(TestConfig.imagePath)
            if (!imageFile.exists()) {
                try {
                    URI(TestConfig.imageUrl).toURL().openStream().use { input ->
                        imageFile.outputStream().use { output -> input.copyTo(output) }
                    }
                } catch (e: Exception) {
                    error("Failed to download Docker image: ${e.message}")
                }
            }
            ProcessBuilder("docker", "load", "-i", TestConfig.imagePath)
                .inheritIO()
                .start()
                .waitFor()
        }

        container = GenericContainer(DockerImageName.parse(TestConfig.imageName))
            .withExposedPorts(exposedPort)
            .withEnv("VERBOSE", "1")
        container.start()
        return container.getMappedPort(exposedPort)
    }

    fun stop() {
        container.stop()
    }

    private fun isImageLoaded(): Boolean {
        val process = ProcessBuilder("docker", "images", "-q", TestConfig.imageName)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText().trim()
        return output.isNotEmpty()
    }
}
