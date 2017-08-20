package edu.knoldus


import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.SalaryDepositActor.DepositStatus
import edu.knoldus.UserAccountGenerator.{AccountCreated, BillerLinkedStatus}
import edu.knoldus.models.{Account, Biller}
import scala.collection.parallel.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 19/8/17.
  */
class DataBase {

  val accountNumToAccount: mutable.ParHashMap[Long, Account] = mutable.ParHashMap()
  val userNames: mutable.ParHashSet[String] = mutable.ParHashSet()
  val accountNumToBiller: mutable.ParHashMap[Long, List[Biller]] = mutable.ParHashMap()

  def addAccount(account: Account): Future[AccountCreated] = {
    Future {
      if (!userNames.contains(account.userName)) {
        accountNumToAccount += (account.accountNumber -> account)
        userNames += account.userName
        AccountCreated(true,account.accountNumber)
      }
      else {
        AccountCreated(false,account.accountNumber)
      }
    }
  }

  def updateAccountBalance(accountNum: Long, balance: Long): Future[DepositStatus] = {
    Future {
      accountNumToAccount.get(accountNum).fold(DepositStatus(false,accountNum,-1L)) { acc =>
        val updatedAccountBalance = acc.updateBalance(balance)
        accountNumToAccount += (accountNum -> updatedAccountBalance)
        DepositStatus(true,accountNum,updatedAccountBalance.balance)
      }
    }
  }

  def getBillersByAccountnum(accountnum: Long): Future[List[Biller]] = {
    Future {
      accountNumToBiller.get(accountnum).fold(List[Biller]()) { billers => billers }
    }
  }

  def getAccountByAccountnum(accountnum: Long): Future[Option[Account]] = {
    Future {
      accountNumToAccount.get(accountnum)
    }
  }

  def addBillerToAccount(accountnum: Long, biller: Biller): Future[BillerLinkedStatus] = {
    Future {
      accountNumToAccount.get(accountnum).fold(BillerLinkedStatus(false,accountnum)) { _ =>
        accountNumToBiller.get(accountnum)
          .fold {
            accountNumToBiller += (accountnum -> List(biller))
          } {
            currentBillers =>
              val allBillers = biller :: currentBillers
              accountNumToBiller += (accountnum -> allBillers)
          }
        BillerLinkedStatus(true,accountnum)
      }
    }
  }

  def payBiller(accountnum: Long, biller: Biller): Future[PaidStatus] = {
    Future {
      val invalidBalance = -1L
      val balance = accountNumToAccount.get(accountnum).fold(invalidBalance) { account => account.balance }
      if (balance > biller.amount) {
        accountNumToBiller.get(accountnum).fold(PaidStatus(accountnum,biller.billerCategory,false)) {
          billers =>
            accountNumToBiller += (accountnum -> (biller :: billers.filter(_.billerCategory != biller.billerCategory)))
            PaidStatus(accountnum,biller.billerCategory,true)
        }
      }
      else {
        PaidStatus(accountnum,biller.billerCategory,false)
      }
    }
  }
}
