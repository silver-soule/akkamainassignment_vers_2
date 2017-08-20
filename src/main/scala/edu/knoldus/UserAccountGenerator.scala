package edu.knoldus

import akka.actor.{Actor, ActorLogging, Props}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import akka.pattern.pipe
import edu.knoldus.models.{Account, Biller}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 19/8/17.
  */

class UserAccountGenerator(database: DataBase) extends Actor with ActorLogging with RequiresMessageQueue[BoundedMessageQueueSemantics] {
  /**
    *
    * @return forwards a request to database repo to check and create a new account if possible
    */
  var accountNum = 0

  override def receive: Receive = {
    case (acc: List[String]) =>
      val origialSender = sender()
      accountNum += 1
      log.info(s"adding a new account with accountnum $accountNum")
      pipe(database.addAccount(Account(accountNum, acc))) to origialSender

    case (accountNumber: Long, biller: Biller) =>
      val originalSender = sender()
      pipe(database.addBillerToAccount(accountNumber, biller)) to originalSender
  }
}

object UserAccountGenerator {

  def props(dataBase: DataBase): Props = Props(classOf[UserAccountGenerator], dataBase)

  case class AccountCreated(status: Boolean, accountNumber: Long)

  case class BillerLinkedStatus(status: Boolean, accountNumber: Long)

}
