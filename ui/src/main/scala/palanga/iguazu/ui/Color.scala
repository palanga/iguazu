package palanga.iguazu.ui

object Color {
  val transparent   = new Color("transparent")
  val antiqueWhite  = new Color("antiquewhite")
  val darkMagenta   = new Color("darkmagenta")
  val darkRed       = new Color("darkred")
  val paleVioletRed = new Color("palevioletred")
  val red           = new Color("red")
  val white         = new Color("white")
}

class Color private (val self: String)
