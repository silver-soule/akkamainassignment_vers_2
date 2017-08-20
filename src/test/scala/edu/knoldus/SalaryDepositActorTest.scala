package edu.knoldus

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
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
class SalaryDepositActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with BeforeAndAfter with MockitoSugar {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  val biller = Biller("InvalidCategory", "panda", 10L, "food", 22L, 1, 1, 0)
  val dataBase = mock[DataBase]
  val salaryDepositActor = system.actorOf(SalaryDepositActor.props(dataBase))
  test("deposit money to account") {
    salaryDepositActor ! ("Neelaksh", 1L, 100L)
    when(dataBase.updateAccountBalance(1L, 100L)) thenReturn Future.successful(DepositStatus(true,1L,100L))
    when(dataBase.getBillersByAccountnum(1L)) thenReturn Future.successful(List(biller))
    expectMsg(TransactionStatus(1L,true))
  }


}
