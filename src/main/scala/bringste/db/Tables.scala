package bringste.db

import java.sql.Timestamp

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}


/**
 * User(id?, name)
 */
case class User(
  id: Option[Int] = None,
  name: String
)

class Users(tag: Tag) extends Table[User](tag, "USER") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)

  def * = (id.?, name) <> (User.tupled, User.unapply)
}


/**
 * Location(id?, name, latitude, longitude)
 */
case class Location(
  id: Option[Int] = None,
  name: String,
  latitude: BigDecimal,
  longitude: BigDecimal
)

class Locations(tag: Tag) extends Table[Location](tag, "LOCATION") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def latitude = column[BigDecimal]("LATITUDE")
  def longitude = column[BigDecimal]("LONGITUDE")

  def * = (id.?, name, latitude, longitude) <> (Location.tupled, Location.unapply)
}


/**
 * ShoppingList(id?, name, status, dueDate, assigneeId?, fromId?, toId?)
 */
case class ShoppingList(
  id: Option[Int] = None,
  name: String,
  status: String = "new",
  dueDate: Timestamp,
  creatorId: Option[Int] = None,
  assigneeId: Option[Int] = None,
  fromId: Option[Int] = None,
  toId: Option[Int] = None
)

class ShoppingLists(tag: Tag) extends Table[ShoppingList](tag, "SHOPPING_LIST") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def status = column[String]("STATUS", O.NotNull)
  def dueDate = column[Timestamp]("DUE_DATE", O.NotNull)

  // foreign keys

  def creatorId = column[Int]("CREATOR_ID", O.NotNull)
  def assigneeId = column[Int]("ASSIGNEE_ID", O.NotNull)
  def fromId = column[Int]("FROM_ID", O.NotNull)
  def toId = column[Int]("TO_ID", O.NotNull)

  // relationships

  def creator: ForeignKeyQuery[Users, User] =
    foreignKey("CREATOR_FK", assigneeId, TableQuery[Users])(_.id)

  def assignee: ForeignKeyQuery[Users, User] =
    foreignKey("ASSIGNEE_FK", assigneeId, TableQuery[Users])(_.id)

  def from: ForeignKeyQuery[Locations, Location] =
    foreignKey("FROM_FK", fromId, TableQuery[Locations])(_.id)

  def to: ForeignKeyQuery[Locations, Location] =
    foreignKey("TO_FK", toId, TableQuery[Locations])(_.id)

  def * = (id.?, name, status, dueDate, creatorId.?, assigneeId.?, fromId.?, toId.?) <> (ShoppingList.tupled, ShoppingList.unapply)
}


/**
 * ShoppingListItem(id?, name, done, listId)
 */
case class ShoppingListItem(
  id: Option[Int] = None,
  name: String,
  done: Boolean = false,
  listId: Int
)

class ShoppingListItems(tag: Tag) extends Table[ShoppingListItem](tag, "SHOPPING_LIST_ITEM") {

  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.NotNull)
  def done = column[Boolean]("DONE")

  // foreign keys

  def listId = column[Int]("LIST_ID", O.NotNull)

  // relationships

  def list: ForeignKeyQuery[ShoppingLists, ShoppingList] =
    foreignKey("LIST_FK", listId, TableQuery[ShoppingLists])(_.id)

  def * = (id.?, name, done, listId) <> (ShoppingListItem.tupled, ShoppingListItem.unapply)
}