package edu.knoldus

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.models.Biller
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 7/8/17.
  */
class BillerPayActor(dataBase: DataBase, billerCategory: String) extends Actor with ActorLogging {
  override def preStart(): Unit = {
    log.info(s"starting actor of$billerCategory")
    super.preStart()
  }

  override def receive: Receive = {
    case (accountNum: Long, biller: Biller) =>
      val originalSender = sender()
      log.info(s"payment to biller type $billerCategory----------->\n")
      val paid = dataBase.payBiller(accountNum, biller.updateBiller())
      paid pipeTo self
    case paidStatus: PaidStatus =>
      log.info(s"${paidStatus.accountNumber} paid $billerCategory :: status : ${paidStatus.status}")
    case error: Throwable =>
      log.error(s"${error.getStackTrace}")

  }
}

object BillerPayActor {
  def props(dataBase: DataBase, billerCategory: String): Props = Props(classOf[BillerPayActor], dataBase, billerCategory)

  case class PaidStatus(accountNumber: Long, billerCategory: String, status: Boolean)

}
