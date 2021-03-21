package palanga.iguazu.api.graphql.queries

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.{ GreaterThanZeroQuantity, Id }
import palanga.iguazu.core.modules.{ Customers, Orders, Stores }
import zio.prelude.NonEmptyList
import zio.stream.ZStream
import zio.{ Has, ZIO }

object types {

  case class QueriesRoot(
    store: StoreArgs => ZIO[Has[Stores], Throwable, StoreNode],
    customer: CustomerArgs => ZIO[Has[Customers], Throwable, CustomerNode],
  )

  case class StoreNode(
    id: Id[Store],
    url: String,
    articles: List[Article],
    orders: ZStream[Has[Orders], Throwable, OrderNode],
  )

  case class CustomerNode(
    id: Id[Customer],
    contactInfo: String,
    orders: ZStream[Has[Orders], Throwable, OrderNode],
  )

  case class OrderNode(
    id: Id[Order],
    articles: NonEmptyList[(Article, GreaterThanZeroQuantity)],
    customer: ZIO[Has[Customers], Throwable, CustomerNode],
    store: ZIO[Has[Stores], Throwable, StoreNode],
  )

  case class StoreArgs(id: Id[Store], customer: Option[Id[Customer]])
  case class CustomerArgs(id: Id[Customer], store: Option[Id[Store]])

}
