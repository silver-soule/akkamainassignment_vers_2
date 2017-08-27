package edu.knoldus.service

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActor, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite, FunSuiteLike}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Neelaksh on 28/8/17.
  */
class SalaryDepositServiceTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender  with BeforeAndAfter {

  override protected def afterAll(): Unit = {
    system.terminate()
  }
  val userName = "Neelaksh"
  val userId = 123
  val amount = 1000
  val salarydepActor = TestProbe()
  val salarydepositservice = new SalaryDepositService(salarydepActor.ref)
  test("testDepositSalary") {

    salarydepActor.setAutoPilot((sender: ActorRef, msg: Any) => {
      val resturnMsg = msg match {
        case (this.userName, this.userId,this.amount) => true
        case _ => false
      }
      sender ! resturnMsg
      TestActor.NoAutoPilot
    })
    salarydepositservice.depositSalary(userName,userId,amount)

  }

}