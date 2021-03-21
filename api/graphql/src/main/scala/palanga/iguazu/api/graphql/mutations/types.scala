package palanga.iguazu.api.graphql.mutations

import palanga.iguazu.core.model.types.{ Article, Customer, Discount, Order, Store }
import palanga.iguazu.core.model.util.{ GreaterThanZeroQuantity, Id }
import palanga.iguazu.core.modules.{ Customers, Orders }
import zio.prelude.NonEmptyList
import zio.{ Has, ZIO }

object types {

  case class MutationsRoot(
    createCustomer: Customer => ZIO[Has[Customers], Throwable, Id[Customer]],
    placeOrder: Order => ZIO[Has[Orders], Throwable, Id[Order]],
    placeAnonymousOrder: AnonymousOrderArgs => ZIO[Has[Orders] with Has[Customers], Throwable, AnonymousOrderResponse],
  )

  case class AnonymousOrderArgs(
    order: AnonymousOrder,
    customer: Customer,
  )

  case class AnonymousOrder(
    store: Id[Store],
    articles: NonEmptyList[(Article, GreaterThanZeroQuantity)],
    discounts: List[Discount] = Nil,
  ) {
    def withCustomer(customer: Id[Customer]): Order = Order(store, customer, articles, discounts)
  }

  case class AnonymousOrderResponse(
    order: Id[Order],
    customer: Id[Customer],
  )

}
