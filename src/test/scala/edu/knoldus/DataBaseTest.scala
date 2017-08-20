package edu.knoldus

import org.scalatest.{AsyncFunSuite, FunSuite}

/**
  * Created by Neelaksh on 19/8/17.
  */

import edu.knoldus.models.{Account, Biller}
import org.scalatest.FunSuite

/**
  * Created by Neelaksh on 6/8/17.
  */
class DataBaseTest extends AsyncFunSuite {

  val db = new DataBase ()
  val accountnum1 = 123
  val accountnum2 = 456
  val initamount1 = 1000
  val initamount2 = 20000
  val account1 = Account(1L, "Neelaksh", "c-123", "silversoule", initamount1)
  val account2 = Account(2L, "Suryansh", "b-213", "potato", initamount2)
  val account3 = Account(3L, "Suryansh", "b-213", "po", initamount2)
  val poor = Account(4L, "Suryansh", "b-213", "pot", 0)
  val invalidUser = Account(5L, "Suryansh", "b-213", "pot", 0)
  db.addAccount(account1)
  val biller = Biller("food", "panda", 1L, "food", 22L, 1, 1, 0)


  test("testUpdateAccountBalance") {
    db.updateAccountBalance(1, initamount1).map (depositStatus => assert(depositStatus.status))
    db.updateAccountBalance(6L,1000L).map(depositStatus => assert(!depositStatus.status))
  }

  test("testGetAccount") {
    db.addAccount(account2)
    db.getAccountByAccountnum(1L).map(account => assert(account.get == account1.updateBalance(initamount1)))
  }

  test("testAddAccount") {
    db.addAccount(account2).map(accountCreated => assert(!accountCreated.status))
    db.addAccount(account3).map(accountCreated => assert(accountCreated.status))
  }

  test("testAddBiller") {
    db.addBillerToAccount(1,biller).map(linked => assert(linked.status))
  }

  test("testPayBIller") {
    db.addAccount(poor)
    db.payBiller(account1.accountNumber,biller).map(paid => assert(paid.status))
    db.payBiller(invalidUser.accountNumber,biller).map(paid => assert(!paid.status))
    db.payBiller(account2.accountNumber,biller).map(paid => assert(!paid.status))
  }

  test("get billers by account number") {
    db.getBillersByAccountnum(2L).map(listOfBillers => assert(listOfBillers.isEmpty))
  }

}