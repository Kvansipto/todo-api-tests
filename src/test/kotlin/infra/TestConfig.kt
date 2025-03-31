package infra

import java.util.*

object TestConfig {

    private val config = Properties().also {
        javaClass.getResourceAsStream("/testconfig.properties")?.use(it::load)
            ?: error("testconfig.properties not found")
    }

    val baseHttpUrl: String get() = config.getProperty("base.url")
    val imagePath: String get() = config.getProperty("docker.image.path")
    val imageUrl: String get() = config.getProperty("docker.image.download")
    val imageName: String get() = config.getProperty("docker.image.name")
    val exposedPort: Int get() = config.getProperty("docker.exposedPort").toInt()
    val baseAuth: String get() = config.getProperty("auth")
}