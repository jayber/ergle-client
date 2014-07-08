package ergle

import java.io.{PrintWriter, File}

import scala.io.Source
import scala.collection.mutable

object PathsConfig {
}

trait PathsConfig {

  val configFile: File = new File(ConfigProvider.parent, "paths.list")

  def read() = {
    def getBlankOpt(value: String) = value match {
      case "" => None
      case _ => Some(value)
    }

    var list = mutable.Buffer[TagAndPath]()
    if (configFile.exists()) list ++= Source.fromFile(configFile).getLines().map {
      line =>
        val parts = line.split(',')
        TagAndPath(
          parts.length match {
            case 3 => getBlankOpt(parts(2))
            case _ => None
          },
          parts.length match {
            case 1 => None
            case _ => getBlankOpt(parts(1))
          },
          parts(0))
    }
    list
  }

  def save(content: mutable.Buffer[String]) {
    val writer = new PrintWriter(configFile)
    try content foreach {
      writer.println
    }
    finally writer.close()
  }
}

case class TagAndPath(shareTo: Option[String], tag: Option[String], path: String)