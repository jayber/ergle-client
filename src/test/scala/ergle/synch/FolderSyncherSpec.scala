package ergle.synch

import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class FolderSyncherSpec extends FlatSpec with MockitoSugar {
  "FolderSyncher" should
    "send new files to ergle" in {
    val path = "testVal"
    val pathFile1 = new File("path1")
    val pathFile2 = new File("path2")

    val lister = mock[FileLister]
    when(lister.list(path)) thenReturn Array(pathFile1, pathFile2)
    val sender = mock[FileSender]

    val folderSyncherImpl: FolderSyncher = new FolderSyncher
    folderSyncherImpl.fileLister = lister
    folderSyncherImpl.fileSender = sender

    folderSyncherImpl.synch(path)

    verify(lister).list(path)
    verify(sender).send(pathFile1)
    verify(sender).send(pathFile2)
  }

}
