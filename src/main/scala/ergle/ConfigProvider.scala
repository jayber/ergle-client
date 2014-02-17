package ergle

import javax.inject.{Named, Singleton}
import com.typesafe.config.{ConfigFactory, Config}
import java.io.File


object ConfigProvider {
  val apiUrlKey: String = "api.url"

  private val parentFile: File = new File(System.getProperty("user.home"), ".ergle")

  def parent: File = {
    parentFile.mkdirs()
    parentFile
  }
}

@Named
@Singleton
class ConfigProvider {
  def config: Config = {
    val configFile: File = new File(ConfigProvider.parent, "application.properties")
    ConfigFactory.parseFile(configFile)
  }
}
