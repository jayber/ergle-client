package ergle.synch

import org.specs2.mutable.Specification
import ergle.PathsConfigExample
import org.specs2.mock.Mockito
import org.specs2.matcher.ThrownExpectations

class MainSpec extends Specification {
  "Main" should {
    "start synching files" in new PathsConfigExample2 {
      /*
            val testValue: String = "testvalue"
            val testValue2: String = "testvalue2"

            save(mutable.Buffer(testValue,testValue2))
            val main: Syncher = new Syncher
            val syncher: FolderSyncher = mock[FolderSyncher]
            main.folderSyncher = syncher

            main.synch()

            there were one(syncher).synch(testValue) andThen one(syncher).synch(testValue2)*/
    }
  }

  class PathsConfigExample2 extends PathsConfigExample with Mockito with ThrownExpectations

}


