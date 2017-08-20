package edu.knoldus.models

/**
  * Created by Neelaksh on 8/8/17.
  */
case class Account(accountNumber: Long, accountHolderName: String, address: String, userName: String, balance: Long) {
  def updateBalance(balance: Long): Account = Account(this.accountNumber, this.accountHolderName, this.address, this.userName, balance + this.balance)
}

object Account {
  def apply(accountNum: Long, inp: List[String]): Account = new Account(accountNum, inp(0), inp(1), inp(2), inp(3).toLong)
}
