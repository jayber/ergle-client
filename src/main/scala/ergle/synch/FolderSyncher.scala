package ergle.synch

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import com.typesafe.scalalogging.slf4j.Logging
import ergle.TagAndPath


@Named
@Singleton
class FolderSyncher extends Logging {

  @Inject
  var fileSender: FileSender = null
  @Inject
  var fileLister: FileLister = null

  def synch(tagAndPath: TagAndPath)(implicit ec: ExecutionContext) {
    logger.debug(s"synching path: $tagAndPath")
    val fileList = fileLister.list(tagAndPath.path)

    fileList.foreach { file =>
      fileSender.send(file, tagAndPath.tag)
    }
  }
}
