package palanga.iguazu.eventsourcing

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import palanga.iguazu.core.model.util.Id.id
import palanga.iguazu.core.modules.Orders
import palanga.iguazu.core.modules.Orders.Filter
import palanga.iguazu.eventsourcing.events.OrderEvent
import palanga.iguazu.eventsourcing.events.OrderEvent.Placed
import palanga.parana.journal.Journal
import palanga.parana.{ EventSource, Reducer }
import zio.stream.UStream
import zio.{ Has, ZLayer }

object EventSourcedOrders {

  private val reducer: Reducer[Order, OrderEvent] = { case (None, Placed(store, customer, articles, discounts)) =>
    Right(Order(store, customer, articles, discounts))
  }

  val live: ZLayer[Journal[OrderEvent], Nothing, Has[Orders]] =
    EventSource.live(reducer).map(_.get).map(new EventSourcedOrders(_)).map(Has(_))

}

class EventSourcedOrders(
  orders: EventSource.Service[Order, OrderEvent]
) extends Orders {

  override def get(id: Id[Order]) = orders.read(id.self)

  override def placeOrder(order: Order) =
    orders
      .persistNewAggregateFromEvent(Placed(order.store, order.customer, order.articles, order.discounts))
      .map(Id.fromPair)

  override def fromFilter(filter: Filter) = filter match {
    case Filter(Some(store), Some(customer)) => ordersByStoreAndCustomer(store)(customer)
    case Filter(Some(store), None)           => ordersByStore(store)
    case Filter(None, Some(customer))        => ordersByCustomer(customer)
    case _                                   => UStream.empty
  }

  private def ordersByStoreAndCustomer(store: Id[Store])(customer: Id[Customer]) =
    orders.readAll.collect {
      case (orderId, order) if order.customer == customer && order.store == store => (id[Order](orderId), order)
    }

  private def ordersByStore(store: Id[Store]) =
    orders.readAll.collect {
      case (orderId, order) if order.store == store => (id[Order](orderId), order)
    }

  private def ordersByCustomer(customer: Id[Customer]) =
    orders.readAll.collect {
      case (orderId, order) if order.customer == customer => (id[Order](orderId), order)
    }

}
