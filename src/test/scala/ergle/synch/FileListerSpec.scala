package ergle.synch

import java.io.File
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class FileListerSpec extends FlatSpec with MockitoSugar {
  //todo: make sure test for excluding .ergle files and other (such as Thumbs.db)
  //todo: deal with subfolders in general
  "FileLister" should "list all files if not tracked yet" in new FileLister {
    val parentDirectoryFile = mock[File]
    val ergleDirectoryFile = mock[File]
    var trackingFilesMockList: Seq[File] = scala.collection.mutable.Seq[File]()
    when(ergleDirectoryFile.exists()).thenReturn(false)
    when(parentDirectoryFile.listFiles(org.mockito.Matchers.any[ErgleFileFilter])) thenReturn Array(new File("file1"), new File("file2"))

    val files = list("directory")

    assert(files.length == 2)
    verify(ergleDirectoryFile, times(1)).exists()
    verify(parentDirectoryFile).listFiles(org.mockito.Matchers.any[ErgleFileFilter])

    trackingFilesMockList.foreach {
      file => verify(file).createNewFile()
    }

    override def file(path: String): File = {
      parentDirectoryFile
    }

    override def fileForParent(parent: File, path: String): File = {
      if (parent == parentDirectoryFile) ergleDirectoryFile
      else {
        val file = mock[File]
        trackingFilesMockList = trackingFilesMockList :+ file
        file
      }
    }
  }

  "FileLister" should "not list unmodified tracked files" in new FileLister {
    val parentDirectoryFile = mock[File]
    val ergleDirectoryFile = mock[File]
    when(ergleDirectoryFile.exists()).thenReturn(true)
    val file1 = mock[File]
    when(file1.getName) thenReturn "file1"
    val file2 = mock[File]
    when(file2.getName) thenReturn "file2"
    val file3 = mock[File]
    when(file3.getName) thenReturn "file3"
    when(file1.lastModified()) thenReturn System.currentTimeMillis() - 1000
    when(file2.lastModified()) thenReturn System.currentTimeMillis()
    when(parentDirectoryFile.listFiles(org.mockito.Matchers.any[ErgleFileFilter])) thenReturn Array(file1, file2, file3)
    val trackFile1 = mock[File]
    val trackFile2 = mock[File]
    val trackFile3 = mock[File]
    when(trackFile1.exists()) thenReturn true
    when(trackFile2.exists()) thenReturn true
    when(trackFile3.exists()) thenReturn false
    when(trackFile1.lastModified()) thenReturn System.currentTimeMillis()
    when(trackFile2.lastModified()) thenReturn System.currentTimeMillis() - 1000

    val files = list("directory")

    assert(files.length == 2)
    assert(files.contains(file2))
    assert(files.contains(file3))
    assert(!files.contains(file1))

    verify(ergleDirectoryFile, times(1)).exists()
    verify(parentDirectoryFile).listFiles(org.mockito.Matchers.any[ErgleFileFilter])

    override def file(path: String): File = {
      parentDirectoryFile
    }

    override def fileForParent(parent: File, path: String): File = {
      if (parent == parentDirectoryFile) ergleDirectoryFile
      else {
        path match {
          case "file1" => trackFile1
          case "file2" => trackFile2
          case "file3" => trackFile3
        }
      }
    }
  }

  "ErgleFileNameFilter" should "not accept files called .ergle" in {
    val filter = new ErgleFileFilter

    assert(!filter.accept(new File(".ergle")))
    assert(!filter.accept(new File(".test")))

    assert(!filter.accept(new File("desktop.ini")))
    assert(!filter.accept(new File("Thumbs.db")))

    val testDir = mock[File]
    when(testDir.isDirectory) thenReturn true
    when(testDir.getName) thenReturn "testDir"
    assert(!filter.accept(testDir))


  }
}
