package palanga.iguazu.ui

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import palanga.iguazu.core.model.types.{ Article, Store, Tag }

object Home {

  case class State(
    store: Var[AsyncState[String, Store]],
    cart: Var[List[Article]],
  )

  object State {
    val empty = State(Var(AsyncState.Loading), Var(Nil))
  }

  sealed trait AsyncState[+E, +A]
  object AsyncState {
    case object Loading        extends AsyncState[Nothing, Nothing]
    case class Ready[T](a: T)  extends AsyncState[Nothing, T]
    case class Failed[E](e: E) extends AsyncState[E, Nothing]
  }

  val store =
    Store(
      "La Tienda de Nube",
      List(
        Article(
          "Trapo de bienvenida",
          "ARS 500",
          List("https://cleanmarket.com.ar/wp-content/uploads/2020/03/trapo-piso-duramas-gris.jpg"),
        ) -> Nil
      ),
    )

  val state = State.empty.copy(store = Var(AsyncState.Ready(store)))

  lazy val view =
    div(
      Renderer.render(state.store)
//      button(
//        "load",
//        onClick --> (_ => state.store.set(AsyncState.Ready(store.now()))),
//      ),
    )

  trait Renderer[A] {
    def render(a: A): HtmlElement
  }

  object Renderer {
    def render[A](a: A)(implicit renderer: Renderer[A]): HtmlElement =
      renderer.render(a)
  }

  implicit val stringRenderer: Renderer[String] = self => div(self)

  implicit def asyncStateRenderer[E, A](implicit
    rendererA: Renderer[A],
    rendererE: Renderer[E],
  ): Renderer[AsyncState[E, A]] = {
    case AsyncState.Loading   => div("Loading...")
    case AsyncState.Ready(a)  => rendererA.render(a)
    case AsyncState.Failed(e) => rendererE.render(e)
  }

  implicit def varRenderer[A](implicit rendererA: Renderer[A]): Renderer[Var[A]] = self =>
    div(child <-- self.signal.map(Renderer.render(_)))

  implicit val articleRenderer: Renderer[Article] = self =>
    div(
      width("160"),
      self.media.map(url => img(src(url), width("inherit"))),
      div(self.name),
      div(self.price),
      onClick --> { _ => Cart.add(self) },
    )

  object Cart {
    def add(article: Article): Unit = state.cart.update(article :: _)
  }

  implicit val storeRenderer: Renderer[Store] = self =>
    div(
      h1(self.url),
      self.articles.map(a => Renderer.render(a._1)),
    )

  implicit class StoreOps(private val self: Store) extends AnyVal {
    def render =
      div(
        h1(self.url),
        self.articles.head.render,
      )
  }

  implicit class ArticleTagsOps(private val self: (Article, List[Tag])) extends AnyVal {
    def render =
      div(
        self._1.name,
        self._1.price,
      )
  }

}
