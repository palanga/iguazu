package palanga.iguazu.eventsourcing

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.{ GreaterThanZeroQuantity, Id }
import zio.prelude.NonEmptyList

object events {

  sealed trait StoreEvent
  object StoreEvent {
    case class Created(url: String)                                  extends StoreEvent
    case class ArticlesUpdated(articles: List[(Article, List[Tag])]) extends StoreEvent
  }

  sealed trait CustomerEvent
  object CustomerEvent {
    case class Created(contactInfo: String) extends CustomerEvent
  }

  sealed trait OrderEvent
  object OrderEvent {
    case class Placed(
      store: Id[Store],
      customer: Id[Customer],
      articles: NonEmptyList[(Article, GreaterThanZeroQuantity)],
      discounts: List[Discount] = Nil,
    ) extends OrderEvent
  }

}
