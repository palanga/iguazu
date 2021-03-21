package palanga.iguazu.core.modules

import zio.{ Has, IO, ZIO }
import zio.json.JsonEncoder

trait Notifications {
  def notify[A](a: A)(implicit tag: JsonEncoder[A]): IO[Throwable, A]
}

object Notifications {
  def notify[A](a: A)(implicit tag: JsonEncoder[A]): ZIO[Has[Notifications], Throwable, A] =
    ZIO.accessM(_.get.notify(a))
}
