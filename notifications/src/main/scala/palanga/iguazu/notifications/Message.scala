package palanga.iguazu.notifications

import palanga.iguazu.core.model.types.Article
import palanga.iguazu.core.model.util.GreaterThanZeroQuantity
import palanga.iguazu.core.modules.Customers
import palanga.iguazu.eventsourcing.events.OrderEvent
import palanga.parana.AggregateId
import zio.prelude.NonEmptyList

object Message {

  def fromEvent(id: AggregateId, event: OrderEvent) = event.asMessage

  implicit class OrderEventShow(private val self: OrderEvent) extends AnyVal {
    def asMessage =
      self match {
        case OrderEvent.Placed(_, customer, articles, _) =>
          Customers
            .get(customer)
            .map(customer => s"""
                                |Te acaban de pedir:
                                |${articles.show}
                                |Contactate con tu cliente:
                                |${customer.contactInfo}
                                |""".stripMargin)

      }
  }

  implicit class ArticlesShow(private val self: NonEmptyList[(Article, GreaterThanZeroQuantity)]) extends AnyVal {
    def show = {
      val total = "self.map { case (article, quantity) => article.price * quantity.self }.sum"
      self.map(_.show).mkString("", "\n", "\n") + "Total: " + total
    }
  }

  implicit class ArticleQuantityShow(private val self: (Article, GreaterThanZeroQuantity)) extends AnyVal {
    def show = self match {
      case (article, quantity) =>
        s"${quantity.self} ${article.name} ${article.price}: ${article.price * quantity.self}"
    }
  }

}
