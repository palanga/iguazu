package palanga.iguazu.core.model

import java.util.UUID

object util {

  final case class Id[T](self: UUID) extends AnyVal
  object Id {
    def id[T](self: UUID): Id[T]            = new Id[T](self)
    def fromPair[T](pair: (UUID, T)): Id[T] = new Id[T](pair._1)
  }

  final class NonZeroQuantity private[util] (val self: Int) extends AnyVal {
    def unary_- = new NonZeroQuantity(-self)
  }
  object NonZeroQuantity {
    def apply(self: Int): Option[NonZeroQuantity]   = if (self == 0) None else Some(new NonZeroQuantity(self))
    def unapply(self: Int): Option[NonZeroQuantity] = apply(self)
  }

  final class GreaterThanZeroQuantity private (val self: Int) extends AnyVal {
    def asNonZero = new NonZeroQuantity(self)
  }
  object GreaterThanZeroQuantity {
    def apply(self: Int): Option[GreaterThanZeroQuantity] =
      if (self <= 0) None else Some(new GreaterThanZeroQuantity(self))
  }

}
