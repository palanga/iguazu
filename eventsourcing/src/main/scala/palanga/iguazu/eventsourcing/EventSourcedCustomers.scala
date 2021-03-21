package palanga.iguazu.eventsourcing

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import palanga.iguazu.core.modules.Customers
import palanga.iguazu.eventsourcing.events.CustomerEvent
import palanga.iguazu.eventsourcing.events.CustomerEvent.Created
import palanga.parana.journal.Journal
import palanga.parana.{ EventSource, Reducer }
import zio.{ Has, IO, ZLayer }

object EventSourcedCustomers {

  private val reducer: Reducer[Customer, CustomerEvent] = { case (None, Created(contactInfo)) =>
    Right(Customer(contactInfo))
  }

  val live: ZLayer[Journal[CustomerEvent], Nothing, Has[Customers]] =
    EventSource.live(reducer).map(_.get).map(new EventSourcedCustomers(_)).map(Has(_))

  val test: ZLayer[Journal[CustomerEvent], Throwable, Has[Customers]] =
    EventSource.live(reducer).map(_.get).tap(data.load).map(new EventSourcedCustomers(_)).map(Has(_))

}

class EventSourcedCustomers(
  customers: EventSource.Service[Customer, CustomerEvent]
) extends Customers {

  override def get(id: Id[Customer]) =
    customers.read(id.self)

  override def create(customer: Customer): IO[Throwable, Id[Customer]] =
    customers
      .persistNewAggregateFromEvent(Created(customer.contactInfo))
      .map(Id.fromPair)

}

object data {

  import java.util.UUID

  def load(source: EventSource.Service[Customer, CustomerEvent]) = {
    val id = UUID.fromString("5bb454a2-8cc1-41c8-9841-4a1c5aa76523")
    source.persist(id)(CustomerEvent.Created("palansky"))
  }

}
