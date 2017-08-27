package edu.knoldus

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import edu.knoldus.BillerPayActor.PaidStatus
import edu.knoldus.DataLoggerActor.LogAllData
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import org.mockito.Mockito._

import scala.concurrent.Future

/**
  * Created by Neelaksh on 28/8/17.
  */
class DataLoggerActorTest extends TestKit(ActorSystem("test-system", ConfigFactory.parseString(
  """
  akka.loggers = ["akka.testkit.TestEventListener"]"""))) with FunSuiteLike
  with BeforeAndAfterAll with MockitoSugar {


  override protected def afterAll(): Unit = {
    system.terminate()
  }
  val mockDataBase = mock[DataBase]
  test("fetch all data from system"){
    when(mockDataBase.getAllInfo())thenReturn Future.successful("2 -> Account(2,Suryansh,c-138,phantomstrike,100)1 -> Account(1,Neelaksh,c-138,silversoul,200)")
    val logActor: ActorRef = system.actorOf(DataLoggerActor.props(mockDataBase))

    EventFilter.info(message = s"2 -> Account(2,Suryansh,c-138,phantomstrike,100)1 -> Account(1,Neelaksh,c-138,silversoul,200)", occurrences = 1) intercept {
      logActor ! LogAllData
    }

  }
}
