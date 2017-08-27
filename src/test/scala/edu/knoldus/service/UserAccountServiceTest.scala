package edu.knoldus.service

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import edu.knoldus.UserAccountGenerator.{AccountCreated, BillerLinkedStatus}
import edu.knoldus.models.Biller
import org.scalatest.{BeforeAndAfterAll, FunSuite, FunSuiteLike}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 28/8/17.
  */
class UserAccountServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender {
  val userAccountService = new UserAccountService

  override protected def afterAll(): Unit = {
    system.terminate()
  }
  val biller = Biller("phone", "panda", 1L, "food", 22L, 1, 1, 0)



  test("TESTING ACCOUNT CREATION")
  {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      var accountnum:Long = 0L
      val resturnMsg = msg match {
        case _: List[String] =>
          accountnum+=1
          AccountCreated(accountnum + 1, true)
      }
      sender ! (resturnMsg)
      TestActor.KeepRunning
    })

    val listOfAccountInfo = List(List("Neel", "B-12, Noida", "silversoule", "2000"),
      List("Suryansh", "A-12, Sector-56, Noida", "potato", "5000"))
    userAccountService.createAccounts(listOfAccountInfo, probe.ref).map(result =>
      assert(result == List(AccountCreated(1L,true), AccountCreated(2L ,true)))
    )
  }



  test("link biller with account") {
    val probe = TestProbe()
    probe.setAutoPilot((sender: ActorRef, msg: Any) => {
      val returnMsg = msg match {
        case (accountnum: Long, _:Biller) => BillerLinkedStatus(accountnum,true)
      }
      sender ! returnMsg
      TestActor.KeepRunning
    })

    userAccountService.linkAccount(1L,biller,probe.ref).map(
      _ == BillerLinkedStatus(1L,true)
    )
  }

}
