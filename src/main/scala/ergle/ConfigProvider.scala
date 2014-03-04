package ergle

import javax.inject.{Named, Singleton}
import com.typesafe.config.{ConfigFactory, Config}
import java.io.{FileWriter, FileReader, File}
import java.util.Properties


object ConfigProvider {
  val emailKey: String = "email"

  val apiUrlKey: String = "api.url"

  private val parentFile: File = new File(System.getProperty("user.home"), ".ergle")

  def configFile: File = new File(ConfigProvider.parent, "application.properties")

  def parent: File = {
    parentFile.mkdirs()
    parentFile
  }

  def save(name: String, value: String) {
    val properties = new Properties()
    properties.load(new FileReader(configFile))
    properties.setProperty(name, value)
    properties.store(new FileWriter(configFile), "")
  }

  def config: Config = {
    ConfigFactory.parseFile(ConfigProvider.configFile)
  }
}

@Named
@Singleton
class ConfigProvider {

  def config: Config = {
    ConfigProvider.config
  }

}
