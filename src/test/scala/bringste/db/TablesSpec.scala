package bringste.db


import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta._

import org.scalatest._


class TableSpec extends FlatSpec with BeforeAndAfter with Matchers {

  val users = TableQuery[Users]
  val locations = TableQuery[Locations]
  val shoppingLists = TableQuery[ShoppingLists]
  val shoppingListItems = TableQuery[ShoppingListItems]

  implicit var session: Session = _


  def createSchema() = (users.ddl ++ locations.ddl ++ shoppingLists.ddl ++ shoppingListItems.ddl).create

  def insertUser(): Int = users += User(None, "Walter")


  before {
    session = Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver").createSession()

    createSchema()
  }

  after {
    session.close()
  }

  "All tables" should "create the schema" in {

    val tables = MTable.getTables().list()

    tables.size should be (4)

    tables.count(_.name.name.equalsIgnoreCase("user")) should be (1)
    tables.count(_.name.name.equalsIgnoreCase("location")) should be (1)
    tables.count(_.name.name.equalsIgnoreCase("shopping_list")) should be (1)
    tables.count(_.name.name.equalsIgnoreCase("shopping_list_item")) should be (1)
  }

  "All tables" should "insert a user" in {
    val insertCount = insertUser()

    insertCount should be (1)
  }

  "All tables" should "query users" in {
    insertUser()

    val results = users.list()

    results.size should be (1)
    results.head.name should be ("Walter")
  }
}