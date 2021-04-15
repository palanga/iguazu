package palanga.iguazu.ui

import com.raquo.laminar.api.L._
import org.scalajs.dom

//import scala.scalajs.js
//import scala.scalajs.js.annotation.JSImport

//@js.native
//@JSImport("stylesheets/main.scss", JSImport.Namespace)
//object Css extends js.Any

object Main {
//  val css: Css.type = Css

  def main(args: Array[String]): Unit = {
    val _ = documentEvents.onDomContentLoaded.foreach { _ =>
      val appContainer = dom.document.querySelector("#app")
      appContainer.innerHTML = ""
      val _            = render(appContainer, Slides.view)
    }(unsafeWindowOwner)
  }
}

object Slides {

//  val view = div("hi")

  val nameVar = Var(initial = "lalala")

  val view = div(
    label("Your name: "),
    input(
      onMountFocus,
      placeholder := "Enter your name here",
      inContext(thisNode => onInput.map(_ => thisNode.ref.value) --> nameVar),
    ),
    span(
      "Hello, ",
      child.text <-- nameVar.signal.map(_.toUpperCase),
    ),
  )

}
