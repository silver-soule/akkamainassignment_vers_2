package edu.knoldus

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.SalaryDepositActor.{DepositStatus, PayBiller, TransactionStatus}
import edu.knoldus.models.Biller
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by Neelaksh on 19/8/17.
  */
class SalaryDepositActor(dataBase: DataBase) extends Actor with ActorLogging {
  var stringToActor: mutable.Map[String, ActorRef] = mutable.Map()
  val billers = List("phone", "electricity", "food", "car", "internet")
  var billerMap: Map[String, ActorRef] = Map()

  override def preStart(): Unit = {
    super.preStart()
    billers.foreach(billerCategory =>
      billerMap += (billerCategory -> context.actorOf(BillerPayActor.props(dataBase, billerCategory))))
  }

  override def receive: Receive = {
    case payBiller: PayBiller =>
      val originalSender = sender()
      billerMap.get(payBiller.biller.billerCategory) match {
        case None =>
          log.info("Invalid Biller o.O")
          sender() ! PaidStatus(payBiller.accountNumber, payBiller.biller.billerCategory, false)
        case Some(billerActor) =>
          log.info("request to pay biller forwarded")
          billerActor ! (payBiller.accountNumber, payBiller.biller)
      }

    case (_: String, accountnum: Long, amount: Long) =>
      val originalSender = sender()
      log.info("sending request to deposit salary")
      pipe(dataBase.updateAccountBalance(accountnum, amount).map(data => (data, originalSender))) to self

    case (depositStatus: DepositStatus, originalSender: ActorRef) =>
      log.info("sending request to fetch billers")
      val billerRequest = dataBase.getBillersByAccountnum(depositStatus.accountNumber)
      log.info("got $billerRequest")
      billerRequest.onComplete {
        case Success(billerList) =>
          log.info("successful retrieval of billers")
          if (billerList.foldLeft(0L)((acc, biller) => acc + biller.amount) < depositStatus.currentBalance) {
            log.info(s"enough money to paybillers in ${depositStatus.accountNumber}")
            billerList.foreach(
              biller => self.tell((depositStatus.accountNumber, biller), originalSender))
            originalSender ! TransactionStatus(depositStatus.accountNumber, true)
          }
          else {
            log.warning(s"not enough balance to pay billers in ${depositStatus.accountNumber}")
            originalSender ! TransactionStatus(depositStatus.accountNumber, false)
          }
        case Failure(ex) => log.error(s"failed to pay billers because of : $ex")
      }
  }
}

object SalaryDepositActor {

  case class Deposit(name: String, accountNum: Long, amount: Long)

  case class TransactionStatus(accountNumber: Long, status: Boolean)

  case class BillersRequest(accountNum: Long)

  case class DepositStatus(status: Boolean, accountNumber: Long, currentBalance: Long)

  case class PayBiller(accountNumber: Long, biller: Biller)

  def props(dataBase: DataBase): Props = Props(classOf[SalaryDepositActor], dataBase)
}
