package edu.knoldus

import akka.actor.ActorSystem
import edu.knoldus.DataLoggerActor.LogAllData
import edu.knoldus.models.Biller
import edu.knoldus.service.{SalaryDepositService, UserAccountService}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 19/8/17.
  */
object Implementation extends App {
  val person1 = List("Neelaksh", "c-138", "silversoul", "100")
  val person2 = List("Suryansh", "c-138", "phantomstrike", "100")
  val actorSystem = ActorSystem("AccountSystemActor")
  val dataBase = new DataBase()
  val accountGeneratorActor = actorSystem.actorOf(UserAccountGenerator.props(dataBase))
  val userAccountService = new UserAccountService
  val status = userAccountService.createAccounts(List(person1,person2),accountGeneratorActor)
  val linkBillerToAccountActor = actorSystem.actorOf(UserAccountGenerator.props(dataBase))
  status.map(
    status=> status.foreach(println(_))
  )
  val biller = Biller("phone", "panda", 1L, "food", 22L, 1, 1, 0)
  val linked = userAccountService.linkAccount(1L,biller,linkBillerToAccountActor)
  val logActor = actorSystem.actorOf(DataLoggerActor.props(dataBase))
  val cancellable =
    actorSystem.scheduler.schedule(
      0 milliseconds,
      20 seconds,
      logActor,
      LogAllData)
  linked.map(println(_))
  val salaryDepositActor = actorSystem.actorOf(SalaryDepositActor.props(dataBase))
  val salaryDepositService =  new SalaryDepositService(salaryDepositActor)
  val deposited = salaryDepositService.depositSalary("neelaksh",1L,100L)
  deposited.map(print(_))
}
