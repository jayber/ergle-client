package ergle.synch

import javax.inject.{Inject, Named, Singleton}
import java.io.File
import play.api.libs.ws.WS
import ergle.ConfigProvider
import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.slf4j.Logging
import scala.util.{Failure, Success}

@Named
@Singleton
class FileSender extends Logging {

  @Inject
  var configProvider: ConfigProvider = null

  def send(file: File)(implicit ec: ExecutionContext) {
    logger.debug(s"sending file: ${file.getName}")

    val apiUrl = configProvider.config.getString(ConfigProvider.apiUrlKey)
    if (apiUrl != null && !apiUrl.isEmpty) {
      val requestHolder = url(apiUrl)
      val email = configProvider.config.getString(ConfigProvider.emailKey)
      if (email != null && !email.isEmpty) {
        val putFuture = requestHolder.withQueryString(("filename", file.getName), ("email", email), ("lastModified", file.lastModified.toString)).put(file)
        putFuture.onComplete {
          case Success(response) =>
            trackFile(file)
            logger.debug(s"file uploaded and tracked. file id: ${response.body}")
          case Failure(t) => logger.error("failed to send file", t)
        }
      } else logger.error(s"no email address configured")
    } else logger.error(s"no api URL")
  }

  def url(url: String) = {
    WS.url(url).withRequestTimeout(1000 * 10)
  }

  def trackFile(file: File) {

    val ergleDirectory = new File(file.getParentFile, FileLister.trackingDirectory)

    ergleDirectory.exists() match {
      case false =>
        logger.debug(s"creating tracking directory: ${ergleDirectory.getPath}")
        ergleDirectory.mkdir()
      case true =>
    }

    val trackFile = new File(ergleDirectory, file.getName)
    trackFile.exists() match {
      case false =>
        logger.debug(s"creating track file: ${trackFile.getPath}")
        trackFile.createNewFile
      case true =>
        logger.debug(s"updating track file: ${trackFile.getPath}")
        trackFile.setLastModified(System.currentTimeMillis())
    }
  }

}
