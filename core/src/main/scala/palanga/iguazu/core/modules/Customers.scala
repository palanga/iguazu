package palanga.iguazu.core.modules

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import zio.{ Has, IO, ZIO }

trait Customers {
  def get(id: Id[Customer]): IO[Throwable, Customer]
  def create(customer: Customer): IO[Throwable, Id[Customer]]
}

object Customers {
  def get(id: Id[Customer])      = ZIO.accessM[Has[Customers]](_.get.get(id))
  def create(customer: Customer) = ZIO.accessM[Has[Customers]](_.get.create(customer))
}
