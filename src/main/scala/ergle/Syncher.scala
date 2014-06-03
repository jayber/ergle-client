package ergle

import javax.inject.{Singleton, Named}
import ergle.synch.FolderSyncher
import com.typesafe.scalalogging.slf4j.Logging
import akka.actor.{ActorRef, ActorSystem, Props, Actor}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Named
@Singleton
class Syncher extends Logging {
  def start() {
    val system = ActorSystem("synchSystem")
    val synchActor = system.actorOf(Props[SynchActor], "synchActor")
    synchActor ! Start
  }
}

case object Start

case object Schedule

class SynchActor extends Actor with PathsConfig with Logging {
  var scheduleActor: ActorRef = null
  override def receive = {
    case Start => {
      synch()

      if (scheduleActor == null) {
        val system = ActorSystem("synchSystem")
        scheduleActor = system.actorOf(Props[ScheduleActor], "scheduleActor")
      }
      scheduleActor ! Schedule
    }
  }

  private def synch() {
    logger.debug("start synching")
    val folderSyncher = Main.ctx.getBean("folderSyncher").asInstanceOf[FolderSyncher]
    val paths = read()
    for (tagAndPath <- paths) {
      folderSyncher.synch(tagAndPath)
    }
  }
}

class ScheduleActor extends Actor {
  var synchActor: ActorRef = null
  override def receive: Actor.Receive = {
    case Schedule => {
      val system = ActorSystem("synchSystem")
      if (synchActor == null) synchActor = system.actorOf(Props[SynchActor], "synchActor")
      system.scheduler.scheduleOnce(1 minutes, synchActor, Start)
    }
  }
}
