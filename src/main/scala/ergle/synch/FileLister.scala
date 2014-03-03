package ergle.synch

import java.io.{FileFilter, File}
import javax.inject.{Singleton, Named}
import com.typesafe.scalalogging.slf4j.Logging

@Named
@Singleton
class FileLister extends Logging {

  def trackFiles(files: Array[File], trackingDirectory: File) {
    trackingDirectory.exists() match {
      case false =>
        logger.debug(s"creating tracking directory: ${trackingDirectory.getPath}")
        trackingDirectory.mkdir()
      case true =>
    }
    files.foreach {
      file =>
        val trackFile = fileForParent(trackingDirectory, file.getName)
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

  def findDelinquentFiles(files: Array[File], trackingDirectory: File): Array[File] = {
    files.filter {
      file => {
        val trackingFile = fileForParent(trackingDirectory, file.getName)
        !trackingFile.exists() || file.lastModified() >= trackingFile.lastModified()
      }
    }
  }

  def list(path: String): Array[File] = {
    val directory = file(path)
    val ergleDirectory = fileForParent(directory, ".ergle")
    val files = makeNullEmptyArray(directory.listFiles(new ErgleFileFilter)).sortWith {
      (f1, f2) => f1.lastModified < f2.lastModified
    }
    val filesToSynch = ergleDirectory.exists() match {
      case false => files
      case _ => findDelinquentFiles(files, ergleDirectory)
    }
    trackFiles(filesToSynch, ergleDirectory)
    filesToSynch
  }


  def makeNullEmptyArray(files: Array[File]): Array[File] = {
    if (files == null) {
      Array()
    } else {
      files
    }
  }

  def file(path: String) = {
    new File(path)
  }

  def fileForParent(parent: File, path: String) = {
    new File(parent, path)
  }
}

class ErgleFileFilter extends FileFilter {
  val exclude = Array("desktop.ini", "Thumbs.db")

  override def accept(file: File): Boolean = {
    !(file.isDirectory || file.getName.startsWith(".") || exclude.contains(file.getName))
  }
}