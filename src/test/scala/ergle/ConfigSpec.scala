package ergle

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.collection.mutable

class ConfigSpec extends Specification {

  "Config" should {
    "read config file" in new PathsConfigExample {

      save(mutable.Buffer("testvalue"))

      val config = read()

      config.isEmpty must beFalse
    }
  }

}

class PathsConfigExample extends PathsConfig with Scope
