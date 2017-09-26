package edu.knoldus

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{CallingThreadDispatcher, EventFilter, ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.models.Biller
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}
import scala.concurrent.Future


/**
  * Created by Neelaksh on 20/8/17.
  */

class BillerPayActorTest extends TestKit(ActorSystem("test-system", ConfigFactory.parseString(
  """
  akka.loggers = ["akka.testkit.TestEventListener"]"""))) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with BeforeAndAfter with MockitoSugar {
  override protected def afterAll(): Unit = {
    system.terminate()
  }

  val accountNumber = 1L
  val dataBase = mock[DataBase]
  val payBiller: ActorRef = system.actorOf(BillerPayActor.props(dataBase, "phone"))
  val biller = Biller("phone", "panda", 1L, "29 sep", 22L, 1, 1, 0)
  val updatedBiller = biller.updateBiller()
  test("paying biller") {
    //when(dataBase.payBiller(accountNumber ,updatedBiller)) thenReturn Future.successful(PaidStatus(accountNumber,biller.billerCategory,true))

    EventFilter.info(message = s"$accountNumber paid ${biller.billerCategory} :: status : true", occurrences = 1) intercept {
      payBiller ! PaidStatus(accountNumber, biller.billerCategory, true)
    }
  }

/*  test("integration test paying biller"){
    when(dataBase.payBiller(accountNumber ,updatedBiller)) thenReturn Future.successful(PaidStatus(accountNumber,biller.billerCategory,true))
    EventFilter.info(message = s"$accountNumber paid ${biller.billerCategory} :: status : true", occurrences = 1) intercept {
      payBiller ! (accountNumber, biller)
    }
  }*/
}