package ergle

import javax.inject.{Singleton, Named}
import ergle.synch.FolderSyncher
import com.typesafe.scalalogging.slf4j.Logging
import akka.actor.{ActorSystem, Props, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Named
@Singleton
class Syncher extends PathsConfig with Logging {
  def start() {
    val system = ActorSystem("synchSystem")
    val synchActor = system.actorOf(Props[SynchActor], "synchActor")
    synchActor ! Start
  }
}

case object Start

case object Schedule

class SynchActor extends Actor with PathsConfig with Logging {

  override def receive = {
    case Start => {
      synch()

      val system = ActorSystem("synchSystem")
      val scheduleActor = system.actorOf(Props[ScheduleActor], "scheduleActor")
      scheduleActor ! Schedule
    }
  }

  private def synch() {
    logger.debug("start synching")
    val paths = read()
    paths.foreach {
      path =>
        val folderSyncher = Main.ctx.getBean("folderSyncher").asInstanceOf[FolderSyncher]
        folderSyncher.synch(path)
    }
  }
}

class ScheduleActor extends Actor {
  override def receive: Actor.Receive = {
    case Schedule => {
      val system = ActorSystem("synchSystem")
      val synchActor = system.actorOf(Props[SynchActor], "synchActor")
      system.scheduler.scheduleOnce(10 minutes, synchActor, Start)
    }
  }
}
