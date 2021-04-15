package palanga.iguazu.core.model

import palanga.iguazu.core.model.util.{ GreaterThanZeroQuantity, Id }
//import palanga.util.price.Price
import zio.prelude.NonEmptyList

object types {

  case class Store(url: String, articles: List[(Article, List[Tag])] = Nil)

  case class Customer(contactInfo: String)

  case class Order(
    store: Id[Store],
    customer: Id[Customer],
    articles: NonEmptyList[(Article, GreaterThanZeroQuantity)],
    discounts: List[Discount] = Nil,
  )

  case class Article(name: String, price: Price)

  type Price = String

  case class Tag(key: String, value: String)

  case class Discount(percentage: BigDecimal, reason: String)

}
