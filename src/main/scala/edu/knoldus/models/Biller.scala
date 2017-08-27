package edu.knoldus.models

import java.util.Calendar

/**
  * Created by Neelaksh on 8/8/17.
  */
case class Biller(billerCategory: String, billerName: String, accountNum: Long, transactionData: String,
  amount: Long, totalIterations: Int, executedIterations: Int, paidAmount: Long) {
  def updateBiller(): Biller = Biller(billerCategory, billerName, accountNum, Calendar.getInstance().getTime().toString,
    amount, totalIterations + 1, executedIterations + 1, paidAmount + amount)
}
