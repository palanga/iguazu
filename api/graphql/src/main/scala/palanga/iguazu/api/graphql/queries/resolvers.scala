package palanga.iguazu.api.graphql.queries

import _root_.zio.stream.ZStream
import palanga.iguazu.api.graphql.queries.types._
import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import palanga.iguazu.core.modules.Orders.Filter
import palanga.iguazu.core.modules._
import zio.{ Has, ZIO }

object resolvers {

  val queriesResolver =
    QueriesRoot(
      args => storeNode(args.id, args.customer),
      args => customerNode(args.id, args.store),
    )

  private def storeNode(id: Id[Store], customer: Option[Id[Customer]]): ZIO[Has[Stores], Throwable, StoreNode] =
    store(id).map(s =>
      StoreNode(id, s.url, s.articles.map(_._1), ordersNode(Filter.withStore(id).withCustomer(customer)))
    )

  private def customerNode(id: Id[Customer], store: Option[Id[Store]]): ZIO[Has[Customers], Throwable, CustomerNode] =
    customer(id).map(c => CustomerNode(id, c.contactInfo, ordersNode(Filter.withCustomer(id).withStore(store))))

  private def ordersNode(filter: Filter): ZStream[Has[Orders], Throwable, OrderNode] =
    orders(filter).map((orderNodeFromOrder(filter) _).tupled)

  private def orderNodeFromOrder(filter: Filter)(id: Id[Order], order: Order) =
    OrderNode(
      id,
      order.articles,
      customerNode(order.customer, filter.store),
      storeNode(order.store, filter.customer),
    )

  private def store(id: Id[Store])       = Stores.get(id)
  private def customer(id: Id[Customer]) = Customers.get(id)
  private def orders(filter: Filter)     = Orders.fromFilter(filter)

}
