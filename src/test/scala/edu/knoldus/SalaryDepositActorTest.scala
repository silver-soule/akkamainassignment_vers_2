package edu.knoldus

import akka.actor.ActorSystem
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.SalaryDepositActor.{DepositStatus, TransactionStatus}
import edu.knoldus.models.Biller
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.Future

/**
  * Created by Neelaksh on 20/8/17.
  */
class SalaryDepositActorTest extends TestKit(ActorSystem("test-system", ConfigFactory.parseString(
  """
  akka.loggers = ["akka.testkit.TestEventListener"]"""))) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with BeforeAndAfter with MockitoSugar {

  override protected def afterAll(): Unit = {
    system.terminate()
  }
  val accountNumber = 1L
  val biller = Biller("InvalidCategory", "panda", 10L, "29 sep", 22L, 1, 1, 0)
  val dataBase = mock[DataBase]
  val salaryDepositActor = system.actorOf(SalaryDepositActor.props(dataBase))
  test("check initial log for depositing money to account") {
    EventFilter.info(message = "sending request to deposit salary", occurrences = 1) intercept {
      salaryDepositActor ! ("ab",accountNumber, 100L)
    }

  }

  test("check logs when trying to deposit money into account and pay billers and having enough money to pay billers"){
    when(dataBase.getBillersByAccountnum(1L)) thenReturn Future.successful(List(biller))
    EventFilter.info(message = s"enough money to paybillers in ${accountNumber}", occurrences = 1) intercept {
      salaryDepositActor ! (DepositStatus(true,1L,100L),testActor)
    }
  }


  test("check logs when trying to deposit money into account and pay billers and not having enough money to pay billers"){
    when(dataBase.getBillersByAccountnum(1L)) thenReturn Future.successful(List(biller))
    EventFilter.warning(message = s"not enough balance to pay billers in ${accountNumber}", occurrences = 1) intercept {
      salaryDepositActor ! (DepositStatus(true,1L,10L),testActor)
    }
  }


}
