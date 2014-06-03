package ergle

import java.io.{PrintWriter, File}

import scala.io.Source
import scala.collection.mutable

object PathsConfig {
}

trait PathsConfig {

  val configFile: File = new File(ConfigProvider.parent, "paths.list")

  def read() = {
    var list = mutable.Buffer[TagAndPath]()
    if (configFile.exists()) list ++= Source.fromFile(configFile).getLines().map {
      line =>
        val parts = line.split('=')
        parts.length match {
          case 2 => TagAndPath(parts(0) match {
            case "" => None
            case _ => Some(parts(0))
          }, parts(1))
          case _ => TagAndPath(None, parts(0))
        }
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

case class TagAndPath(tag: Option[String], path: String)