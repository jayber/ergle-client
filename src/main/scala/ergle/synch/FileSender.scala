package ergle.synch

import javax.inject.{Inject, Named, Singleton}
import java.io.File
import play.api.libs.ws.WS
import ergle.{UploadFolder, ConfigProvider}
import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.slf4j.Logging
import scala.util.{Failure, Success}

@Named
@Singleton
class FileSender extends Logging {

  @Inject
  var configProvider: ConfigProvider = null

  def send(file: File, uploadFolder: UploadFolder)(implicit ec: ExecutionContext) {
    logger.debug(s"sending file: ${file.getName}")

    val apiUrl = configProvider.config.getString(ConfigProvider.apiUrlKey)
    if (apiUrl != null && !apiUrl.isEmpty) {
      val email = configProvider.config.getString(ConfigProvider.emailKey)
      if (email != null && !email.isEmpty) {
        upload(file: File, apiUrl.replace("$email", email), email, uploadFolder)
      } else logger.error(s"no email address configured")
    } else logger.error(s"no api URL")
  }

  def upload(file: File, apiUrl: String, email: String, uploadFolder: UploadFolder)(implicit ec: ExecutionContext) {
    var requestHolder = url(apiUrl).withQueryString(("filename", file.getName), ("email", email), ("lastModified", file.lastModified.toString))
    requestHolder = uploadFolder.tag match {
      case Some(tagName) => requestHolder.withQueryString(("tag", tagName))
      case None => requestHolder
    }
    requestHolder = uploadFolder.shareTo match {
      case Some(shareTo) => requestHolder.withQueryString(("shareTo", shareTo))
      case None => requestHolder
    }
    val putFuture = requestHolder.put(file)
    val start = System.currentTimeMillis()
    putFuture.onComplete {
      case Success(response) =>
        trackFile(file)
        val end = System.currentTimeMillis()
        logger.debug(s"file uploaded and tracked. file id: ${response.body}. Time ${end - start}")
      case Failure(t) => logger.error("failed to send file", t)
    }
  }

  def url(url: String) = {
    WS.url(url)
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
