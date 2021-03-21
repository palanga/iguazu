package palanga.iguazu.eventsourcing

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import palanga.iguazu.core.modules.Stores
import palanga.iguazu.eventsourcing.events.StoreEvent
import palanga.iguazu.eventsourcing.events.StoreEvent.{ ArticlesUpdated, Created }
import palanga.parana.journal.Journal
import palanga.parana.{ EventSource, Reducer }
import zio.{ Has, ZLayer }

object EventSourcedStores {

  private val reducer: Reducer[Store, StoreEvent] = {
    case (None, Created(url))                     => Right(Store(url))
    case (Some(store), ArticlesUpdated(articles)) => Right(store.copy(articles = articles))
  }

  val live: ZLayer[Journal[StoreEvent], Nothing, Has[Stores]] =
    EventSource.live(reducer).map(_.get).map(new EventSourcedStores(_)).map(Has(_))

  val test: ZLayer[Journal[StoreEvent], Throwable, Has[Stores]] =
    EventSource.live(reducer).map(_.get).tap(datasa.load).map(new EventSourcedStores(_)).map(Has(_))

}

class EventSourcedStores(
  stores: EventSource.Service[Store, StoreEvent]
) extends Stores {
  override def get(id: Id[Store]) = stores.read(id.self)
}

object datasa {

  import palanga.util.price.Currency.ARS

  import java.util.UUID

  def load(source: EventSource.Service[Store, StoreEvent]) = {
    val id = UUID.fromString("1b61773c-2c78-4482-9e5a-bf6d0043e141")
    source.persist(id)(storeEvents.head) *> source.persist(id)(storeEvents.last)
  }

  val storeEvents =
    List(
      StoreEvent.Created("latiendadenube.com.ar"),
      StoreEvent.ArticlesUpdated(
        List(
          (
            Article("Foto en la playa", ARS * 120),
            List(Tag("formato", "cuadrado"), Tag("color", "blanco"), Tag("color", "negro")),
          ),
          (
            Article("Trapo de bienvenida", ARS * 230),
            List(Tag("material", "algodon"), Tag("color", "blanco")),
          ),
        )
      ),
    )

}
