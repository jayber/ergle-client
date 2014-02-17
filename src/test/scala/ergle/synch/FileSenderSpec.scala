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

    val file = new File("file")
    val requestHolder = mock[WSRequestHolder]
    when(requestHolder.withQueryString(("filename", "file"))) thenReturn requestHolder
    val future = mock[Future[Response]]
    when(requestHolder.put(file)) thenReturn future

    send(file)

    verify(requestHolder).put(file)

    override def url(url: String) = {
      url === testApiUrl
      requestHolder
    }

  }
}
