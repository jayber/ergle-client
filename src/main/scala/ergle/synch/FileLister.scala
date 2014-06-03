package ergle.synch

import java.io.{FileFilter, File}
import javax.inject.{Singleton, Named}
import com.typesafe.scalalogging.slf4j.Logging

object FileLister {

  val trackingDirectory = ".ergle"

}

@Named
@Singleton
class FileLister extends Logging {

  def file(path: String) = {
    new File(path).getCanonicalFile
  }

  def fileForParent(parent: File, path: String) = {
    new File(parent, path)
  }

  def ergleDirectory(directory: File) = fileForParent(directory, FileLister.trackingDirectory)

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
    val files = makeNullEmptyArray(directory.listFiles(new ErgleFileFilter)).sortWith {
      (f1, f2) => f1.lastModified < f2.lastModified
    }
    logger.debug(s"directory contains: ${files.mkString(";")}")

    val filesToSynch = ergleDirectory(directory).exists() match {
      case false => files
      case _ => findDelinquentFiles(files, ergleDirectory(directory))
    }
    logger.debug(s"filtered directory contains: ${filesToSynch.mkString(";")}")

    filesToSynch
  }


  def makeNullEmptyArray(files: Array[File]): Array[File] = {
    if (files == null) {
      Array()
    } else {
      files
    }
  }
}

class ErgleFileFilter extends FileFilter {
  //todo: add .lnk files to exclusions
  val exclude = Array("desktop.ini", "Thumbs.db")

  override def accept(file: File): Boolean = {
    !(file.isDirectory || file.getName.startsWith(".") || exclude.contains(file.getName))
  }
}