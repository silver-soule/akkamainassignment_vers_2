package edu.knoldus

import akka.actor.{Actor, ActorLogging, Props}
import edu.knoldus.DataLoggerActor.LogAllData
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 27/8/17.
  */
class DataLoggerActor(dataBase: DataBase) extends Actor with ActorLogging {
  override def receive: Receive = {
    case LogAllData =>
      dataBase.getAllInfo().map { data =>
        log.info(s"$data")
      }
  }
}

object DataLoggerActor {

  case object LogAllData

  def props(dataBase: DataBase): Props = Props(classOf[DataLoggerActor], dataBase)

}
