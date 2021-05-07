package palanga.iguazu.ui

import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.html
import palanga.iguazu.ui.View.{ Dimension, DisplayType, IntOps }

import scala.language.postfixOps

object View {

  implicit class IntOps(self: Int) {
    def percent = new Dimension(s"$self%")
    def px      = new Dimension(s"$self")
  }

  val horizontal = View(_display = "flex", _direction = "row", _height = 100 percent, _width = Dimension.auto)
  val vertical   = View(_display = "flex", _direction = "column", _height = Dimension.auto, _width = 100 percent)

  class Dimension(val self: String)
  object Dimension {
    val auto = new Dimension("auto")
  }

  type DisplayType = String

}

case class View private (
  private val _display: DisplayType,
  private val _direction: String,
  private val _height: Dimension,
  private val _width: Dimension,
  private val _backgroundColor: Color = Color.transparent,
  private val _margin: Dimension = 0 px,
  private val _children: List[View] = Nil,
) {

  def render: ReactiveHtmlElement[html.Div] =
    div(
      L.display := _display,
      L.flexDirection := _direction,
      L.height := _height.self,
      L.width := _width.self,
      L.backgroundColor := _backgroundColor.self,
      L.margin := _margin.self,
      _children.map(_.render),
    )

  def children(views: View*) = copy(_children = views.toList)
  def height(h: Dimension)   = copy(_height = h)
  def width(w: Dimension)    = copy(_width = w)
  def margin(m: Dimension)   = copy(_margin = m)
  def color(c: Color): View  = copy(_backgroundColor = c)

}
