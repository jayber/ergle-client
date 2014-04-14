package ergle

import javax.inject.{Named, Singleton}
import com.typesafe.config.{ConfigValueFactory, ConfigFactory, Config}
import java.io.{FileWriter, FileReader, File}
import java.util.Properties


object ConfigProvider {
  val emailKey: String = "email"

  val apiUrlKey: String = "api.url"

  val defaultApiValue: String = "http://ergle.net:9002/files/"

  val defaultValues = ConfigFactory.empty().withValue(emailKey, ConfigValueFactory.fromAnyRef("")).withValue(apiUrlKey, ConfigValueFactory.fromAnyRef(defaultApiValue))

  private val parentFile: File = new File(System.getProperty("user.home"), ".ergle")

  def configFile: File = new File(ConfigProvider.parent, "application.properties")

  def parent: File = {
    parentFile.mkdirs()
    parentFile
  }

  def save(name: String, value: String) {
    val properties = new Properties()
    if (!configFile.exists()) configFile.createNewFile()
    properties.load(new FileReader(configFile))
    properties.setProperty(name, value)
    properties.store(new FileWriter(configFile), "")
  }

  def config: Config = {
    ConfigFactory.parseFile(ConfigProvider.configFile).withFallback(defaultValues)
  }
}

@Named
@Singleton
class ConfigProvider {

  def config: Config = {
    ConfigProvider.config
  }

}
