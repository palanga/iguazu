package palanga.iguazu.api.graphql.mutations

import palanga.iguazu.api.graphql.mutations.types.{ AnonymousOrder, AnonymousOrderResponse, MutationsRoot }
import palanga.iguazu.core.model.types.{ Customer, Order }
import palanga.iguazu.core.modules.{ Customers, Orders }

object resolvers {

  val mutationsResolver =
    MutationsRoot(
      createCustomer,
      placeOrder,
      args => placeAnonymousOrder(args.order, args.customer),
    )

  private def createCustomer(customer: Customer) = Customers.create(customer)

  private def placeOrder(order: Order) = Orders.placeOrder(order)

  private def placeAnonymousOrder(order: AnonymousOrder, customer: Customer) =
    for {
      customerId <- Customers.create(customer)
      orderId    <- Orders.placeOrder(order.withCustomer(customerId))
    } yield AnonymousOrderResponse(orderId, customerId)

}
