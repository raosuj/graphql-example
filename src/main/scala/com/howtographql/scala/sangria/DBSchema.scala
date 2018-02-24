package com.howtographql.scala.sangria

import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps


object DBSchema {

  /**
    * Load schema and populate sample data withing this Sequence od DBActions
    */

  class LinksTable(tag: Tag) extends Table[Link](tag, "LINKS"){
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url = column[String] ("URL")
    def description = column[String]("DESCRIPTION")

    def * = (id, url, description) <> ((Link.apply _).tupled, Link.unapply)
  }

  val Links = TableQuery[LinksTable]

  val databaseSetup = DBIO.seq(
    Links.schema.create,

    Links forceInsertAll Seq(
      Link(1, "http://howtographql.com", "Awesome community driven GraphQL tutorial"),
      Link(2, "http://graphql.org", "Official GraphQL webpage"),
      Link(3, "https://facebook.github.io/graphql/", "GraphQL specification")
    )
  )

  case class DAO(db: Database) {
    def allLinks = db.run(Links.result)

  }

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")
    //TODO: remove
    Await.result(db.run(databaseSetup), 10 seconds)
    new DAO(db)
  }
}
