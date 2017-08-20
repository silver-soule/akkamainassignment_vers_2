package edu.knoldus.service

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import edu.knoldus.SalaryDepositActor.TransactionStatus
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
  * Created by Neelaksh on 6/8/17.
  */
class SalaryDepositService(salaryDepositActor: ActorRef) {
  def depositSalary(name: String, accountNumber: Long, amount: Long): Future[TransactionStatus] = {
    implicit val timeout = Timeout(1000 seconds)
    (salaryDepositActor ? (name, accountNumber, amount)).mapTo[TransactionStatus]
  }
}
