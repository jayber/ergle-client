package ergle.synch

import java.io.File
import play.api.libs.ws.WS.WSRequestHolder
import ergle.ConfigProvider
import com.typesafe.config.Config
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.ws.Response
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class FileSenderSpec extends FlatSpec with MockitoSugar {
  "FileSender" should
    "send files to web api" in new FileSender {
    configProvider = mock[ConfigProvider]
    val config = mock[Config]
    when(configProvider.config) thenReturn config
    val testApiUrl = "testApiUrl"
    when(config.getString(ConfigProvider.apiUrlKey)) thenReturn testApiUrl
    val email = "email"
    when(config.getString(ConfigProvider.emailKey)) thenReturn email

    val (file, requestHolder) = setUpOtherMocks(email)

    send(file, None)

    verify(requestHolder).put(file)

    override def url(url: String) = {
      url === testApiUrl
      requestHolder
    }
  }

  "FileSender" should
    "NOT send files without email address configured" in new FileSender {
    configProvider = mock[ConfigProvider]
    val config = mock[Config]
    when(configProvider.config) thenReturn config
    val testApiUrl = "testApiUrl"
    when(config.getString(ConfigProvider.apiUrlKey)) thenReturn testApiUrl
    val email = "email"
    when(config.getString(ConfigProvider.emailKey)) thenReturn null

    val (file, requestHolder) = setUpOtherMocks(email)

    send(file, None)

    verifyNoMoreInteractions(requestHolder)

    override def url(url: String) = {
      url === testApiUrl
      requestHolder
    }
  }

  "FileSender" should
    "NOT send files without api URL configured" in new FileSender {
    configProvider = mock[ConfigProvider]
    val config = mock[Config]
    when(configProvider.config) thenReturn config
    val testApiUrl = "testApiUrl"
    when(config.getString(ConfigProvider.apiUrlKey)) thenReturn null
    val email = "email"
    when(config.getString(ConfigProvider.emailKey)) thenReturn email

    val (file, requestHolder) = setUpOtherMocks(email)

    send(file, None)

    verifyNoMoreInteractions(requestHolder)

    override def url(url: String) = {
      url === testApiUrl
      requestHolder
    }
  }


  def setUpOtherMocks(email: String) = {
    val file = mock[File]
    when(file.getName) thenReturn "file"
    val modDate = System.currentTimeMillis()
    when(file.lastModified) thenReturn modDate
    val requestHolder = mock[WSRequestHolder]
    when(requestHolder.withQueryString(("filename", "file"), ("email", email), ("lastModified", modDate.toString))) thenReturn requestHolder
    val future = mock[Future[Response]]
    when(requestHolder.put(file)) thenReturn future
    (file, requestHolder)
  }
}
