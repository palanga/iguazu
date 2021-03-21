package palanga.iguazu.core.modules

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import palanga.iguazu.core.modules.Orders.Filter
import zio.stream.{ Stream, ZStream }
import zio.{ Has, IO, ZIO }

trait Orders {
  def get(id: Id[Order]): IO[Throwable, Order]
  def placeOrder(order: Order): IO[Throwable, Id[Order]]
  def fromFilter(filter: Filter): Stream[Throwable, (Id[Order], Order)]
}

object Orders {

  case class Filter private (
    store: Option[Id[Store]] = None,
    customer: Option[Id[Customer]] = None,
  ) {
    def withStore(id: Option[Id[Store]])       = new Filter(store = id, customer)
    def withCustomer(id: Option[Id[Customer]]) = new Filter(store, customer = id)
  }

  object Filter {
    def withStore(id: Id[Store])       = new Filter(store = Some(id))
    def withCustomer(id: Id[Customer]) = new Filter(customer = Some(id))
  }

  def get(id: Id[Order]) =
    ZIO.accessM[Has[Orders]](_.get.get(id))

  def placeOrder(order: Order) =
    ZIO.accessM[Has[Orders]](_.get.placeOrder(order))

  def fromFilter(filter: Filter) =
    ZStream.accessStream[Has[Orders]](_.get.fromFilter(filter))

}
