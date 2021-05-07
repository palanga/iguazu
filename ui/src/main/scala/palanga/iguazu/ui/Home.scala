package palanga.iguazu.ui

import palanga.iguazu.ui.View.IntOps

import scala.language.postfixOps

object Home {
  val view =
    View.horizontal
      .color(Color.antiqueWhite)
      .children(
        View.vertical
          .children(
            View.horizontal
              .color(Color.red)
              .margin(8 px),
            View.horizontal
              .color(Color.darkRed)
              .margin(8 px),
          ),
        View.vertical
          .children(
            View.horizontal
              .color(Color.paleVioletRed)
              .margin(8 px),
            View.horizontal
              .color(Color.darkMagenta)
              .margin(8 px),
          ),
      )
}
