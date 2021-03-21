package palanga.iguazu.notifications
import palanga.iguazu.core.modules.Notifications
import palanga.iguazu.notifications.DiscordNotifications.Config
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.client3.{ basicRequest, SttpBackend, UriContext }
import sttp.model.MediaType
import zio.json.{ JsonEncoder, _ }
import zio.{ Has, Task, ZIO, ZLayer }

object DiscordNotifications {

  case class Config(webhookId: Long, token: String)

  val live: ZLayer[Has[Config], Throwable, Has[Notifications]] =
    ZIO
      .environment[Has[Config]]
      .map(_.get)
      .map(DiscordNotifications(_) _)
      .toManaged_
      .flatMap(HttpClientZioBackend.managed().map(_))
      .toLayer

}

case class DiscordNotifications(config: Config)(backend: SttpBackend[Task, Any]) extends Notifications {

  override def notify[A](a: A)(implicit encoder: JsonEncoder[A]) =
    basicRequest
      .post(url)
      .contentType(MediaType.ApplicationJson)
      .body(discordMessage(a.toJson))
      .send(backend)
      .as(a)

  def discordMessage(message: String) = s"""{"content": $message}"""

  private val url = uri"https://discord.com/api/webhooks/${config.webhookId}/${config.token}"

}
