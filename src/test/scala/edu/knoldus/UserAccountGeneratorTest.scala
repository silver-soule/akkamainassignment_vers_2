package edu.knoldus

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import edu.knoldus.UserAccountGenerator.AccountCreated
import edu.knoldus.models.Account
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

import scala.concurrent.Future

/**
  * Created by Neelaksh on 20/8/17.
  */
class UserAccountGeneratorTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike
  with BeforeAndAfterAll with ImplicitSender with BeforeAndAfter with MockitoSugar {

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  val dataBase = mock[DataBase]
  val userAccountGenerator = system.actorOf(UserAccountGenerator.props(dataBase))
  val accountNumber = 1L
  val person1 = List("Neelaksh", "c-138", "silversoul", "100")
  test("create user account") {
    when(dataBase.addAccount(Account(accountNumber, person1))) thenReturn Future.successful(AccountCreated(accountNumber,true))
    userAccountGenerator ! person1
    expectMsg(AccountCreated(accountNumber,true))
  }

  test("fail to create a new account") {
    when(dataBase.addAccount(Account(2L, person1))) thenReturn Future.successful(AccountCreated(2L,false))
    userAccountGenerator ! person1
    expectMsg(AccountCreated( 2L,false))
  }

}
