package edu.knoldus

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.SalaryDepositActor.PayBiller
import edu.knoldus.models.Biller
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite, FunSuiteLike}

import scala.concurrent.Future

/**
  * Created by Neelaksh on 20/8/17.
  */

class BillerPayActorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender  with BeforeAndAfter with MockitoSugar {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  val accountNumber = 1L
  val dispatcherId = CallingThreadDispatcher.Id
  val dataBase = mock[DataBase]
  val payBiller: ActorRef = system.actorOf(BillerPayActor.props(dataBase,"phone").withDispatcher(dispatcherId))
  val biller = Biller("phone", "panda", 1L, "food", 22L, 1, 1, 0)
  val updatedBiller = biller.updateBiller()

  test("paying biller") {
    when(dataBase.payBiller(accountNumber ,updatedBiller)) thenReturn Future.successful(PaidStatus(accountNumber,biller.billerCategory,true))

    EventFilter.info(message = s"$accountNumber paid ${biller.billerCategory}", occurrences = 1) intercept{
        payBiller ! (accountNumber,biller)
      }
  }

/*  test("paying biller failed") {
    when(dataBase.payBiller(accountNumber ,this.biller.updateBiller())) thenReturn Future.successful(PaidStatus(accountNumber,biller.billerCategory,false))
    payBiller ! (accountNumber, this.biller)
    expectMsg(PaidStatus(accountNumber,biller.billerCategory,false))
  }*/
}