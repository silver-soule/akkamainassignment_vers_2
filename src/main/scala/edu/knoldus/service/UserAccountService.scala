package edu.knoldus.service

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import edu.knoldus.UserAccountGenerator.{AccountCreated, BillerLinkedStatus}
import edu.knoldus.models.Biller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

/**
  * Created by Neelaksh on 19/8/17.
  */
class UserAccountService {
  def createAccounts(accounts: List[List[String]], accountGeneratorRef: ActorRef): Future[List[AccountCreated]] = {
    implicit val timeout = Timeout(10 seconds)
    val createdAccounts =
      for {
        account <- accounts
        accountnumToBool = (accountGeneratorRef ? account).mapTo[AccountCreated]
      } yield accountnumToBool
    Future.sequence(createdAccounts)
  }

  def linkAccount(accountnum: Long, biller: Biller, accountBillerLinker: ActorRef): Future[BillerLinkedStatus] = {
    implicit val timeout = Timeout(10 seconds)
    (accountBillerLinker ? (accountnum, biller)).mapTo[BillerLinkedStatus]
  }

}
