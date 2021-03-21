package palanga.iguazu.api.graphql

import palanga.aconcagua
import palanga.iguazu.core.modules.{ Customers, Notifications, Orders, Stores }
import palanga.iguazu.eventsourcing.events.{ CustomerEvent, OrderEvent, StoreEvent }
import palanga.iguazu.eventsourcing.{ EventSourcedCustomers, EventSourcedOrders, EventSourcedStores }
import palanga.iguazu.notifications.{ DiscordNotifications, Message }
import palanga.parana.{ journal, AggregateId }
import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import zio.config.yaml.YamlConfigSource
import zio.magic._
import zio.{ Has, ZIO }

import java.nio.file.Path

object App extends zio.App {

  type Dependencies = Has[Customers] with Has[Stores] with Has[Orders]

  override def run(args: List[String]) =
    aconcagua.graphql
      .app(graphql.api)
      .instrumented
      .withDefaultMetrics
      .run
      .injectCustom(
        config.toLayer.project(_.discord),
        DiscordNotifications.live,
        journal.inMemory[CustomerEvent].toLayer,
        journal.inMemory[StoreEvent].toLayer,
        journal.decorator[OrderEvent].tap(notify).decorate(journal.inMemory).toLayer,
        EventSourcedCustomers.test,
        EventSourcedStores.test,
        EventSourcedOrders.live,
      )
      .exitCode

  case class Config(discord: DiscordNotifications.Config)

  private val configDescriptor = descriptor[Config]

  private val config =
    ZIO fromEither
      YamlConfigSource
        .fromYamlPath(Path.of(System.getProperty("user.home") + "/.secrets/iguazu.yml"))
        .flatMap(s => read(configDescriptor from s))

  private def notify(id: AggregateId, event: OrderEvent) =
    Message.fromEvent(id, event).flatMap(Notifications.notify[String]).fork

}
