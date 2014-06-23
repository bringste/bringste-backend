package db

import org.scalatest._
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends FunSuite with BeforeAndAfter {

  val users = TableQuery[Users]
  val locations = TableQuery[Locations]
  val shoppingLists = TableQuery[ShoppingLists]
  val shoppingListItems = TableQuery[ShoppingListItems]

  implicit var session: Session = _

  def createSchema() = (users.ddl ++ locations.ddl ++ shoppingLists.ddl ++ shoppingListItems.ddl).create

  def insertUser(): Int = users += User(None, "Walter")

  before {
    session = Database.forURL("jdbc:h2:mem:test", driver = "org.h2.Driver").createSession()
  }

  test("Creating the Schema works") {
    createSchema()

    val tables = MTable.getTables().list()

    assert(tables.size == 4)
    assert(tables.count(_.name.name.equalsIgnoreCase("user")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("location")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("shopping_list")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("shopping_list_item")) == 1)
  }

  test("Inserting a User works") {
    createSchema()

    val insertCount = insertUser()
    assert(insertCount == 1)
  }

  test("Query Users works") {
    createSchema()
    insertUser()

    val results = users.list()
    assert(results.size == 1)
    assert(results.head.name == "Walter")
  }

  after {
    session.close()
  }

}