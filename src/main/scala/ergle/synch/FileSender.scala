package ergle.synch

import javax.inject.{Inject, Named, Singleton}
import java.io.File
import play.api.libs.ws.WS
import ergle.ConfigProvider
import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.slf4j.Logging

@Named
@Singleton
class FileSender extends Logging {

  @Inject
  var configProvider: ConfigProvider = null

  def send(file: File)(implicit ec: ExecutionContext) {
    logger.debug(s"sending file: ${file.getName}")

    val apiUrl = configProvider.config.getString(ConfigProvider.apiUrlKey)
    if (apiUrl != null) {
      val requestHolder = url(apiUrl)
      val email = configProvider.config.getString(ConfigProvider.emailKey)
      if (email != null) {
        requestHolder.withQueryString(("filename", file.getName), ("email", email), ("lastModified", file.lastModified.toString)).put(file).map {
          response =>
            logger.debug(s"file id: ${response.body}")
        }
      } else logger.debug(s"no email address configured")
    } else logger.debug(s"no api URL")
  }

  def url(url: String) = {
    WS.url(url).withRequestTimeout(1000)
  }
}
