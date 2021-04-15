package palanga.iguazu.api.graphql

import caliban.GraphQL.graphQL
import caliban.schema.{ ArgBuilder, GenericSchema, Schema }
import caliban.{ CalibanError, GraphQL, RootResolver }
import palanga.iguazu.api.graphql.App.Dependencies
import palanga.iguazu.api.graphql.mutations.resolvers.mutationsResolver
import palanga.iguazu.api.graphql.queries.resolvers.queriesResolver
import palanga.iguazu.api.graphql.queries.types.{ CustomerNode, OrderNode, StoreNode }
import palanga.iguazu.core.model.util.{ GreaterThanZeroQuantity, NonZeroQuantity }
//import palanga.util.price.Price
import zio.prelude.NonEmptyList

object graphql extends GenericSchema[Dependencies] {

//  implicit val priceSchema: Schema[Any, Price]    = Schema.stringSchema.contramap(_.toString)
//  implicit val priceArgBuilder: ArgBuilder[Price] = ArgBuilder.string.map(Price.fromStringUnsafe)

  implicit val nonZeroArgBuilder: ArgBuilder[NonZeroQuantity] =
    ArgBuilder.int.flatMap(NonZeroQuantity(_).toRight(CalibanError.ExecutionError("zero quantity")))

  implicit val greaterThanZeroArgBuilder: ArgBuilder[GreaterThanZeroQuantity] =
    ArgBuilder.int
      .flatMap(GreaterThanZeroQuantity(_).toRight(CalibanError.ExecutionError("not greater than zero quantity")))

  implicit def nonEmptyListSchema[T](implicit ev: Schema[Any, T]): Schema[Any, NonEmptyList[T]] =
    Schema.listSchema[Any, T].contramap(_.toList)

  implicit def nonEmptyListArgBuilder[T](implicit ev: ArgBuilder[T]): ArgBuilder[NonEmptyList[T]] =
    ArgBuilder.list[T].flatMap(NonEmptyList.fromIterableOption(_).toRight(CalibanError.ExecutionError("empty list")))

  implicit val on = gen[OrderNode]
  implicit val sn = gen[StoreNode]
  implicit val cn = gen[CustomerNode]

  val api: GraphQL[Dependencies] =
    graphQL(
      RootResolver(
        queriesResolver,
        mutationsResolver,
      )
    )

}
