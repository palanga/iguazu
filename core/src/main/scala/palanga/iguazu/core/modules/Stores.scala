package palanga.iguazu.core.modules

import palanga.iguazu.core.model.types._
import palanga.iguazu.core.model.util.Id
import zio.{ Has, IO, ZIO }

trait Stores {
  def get(id: Id[Store]): IO[Throwable, Store]
}

object Stores {
  def get(id: Id[Store]): ZIO[Has[Stores], Throwable, Store] = ZIO.accessM[Has[Stores]](_.get.get(id))
}
