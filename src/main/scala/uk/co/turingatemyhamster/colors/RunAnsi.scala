package uk.co.turingatemyhamster.colors

import Ansi._

/**
 * Created by nmrp3 on 22/12/13.
 */
object RunAnsi {

  def main(args: Array[String]) {

    val greeting = "hello"
    val person = "mum"

    println("1")
    println(esc"Dear $person, please let me say a heart-felt $greeting this Christmas!")

    println("2")
    println(esc"I would \italic{love} to see you soon.")

    println("3")
    println(esc"If you \bold{please} would write,")

    println("4")
    println(esc"then I'd \red{visit} \green{more} \blue{often}")

    println("5")
    println(esc"We \italic{wish you a \bold{merry} \blink{Christmas}} and a \Underline{happy} new year")
  }

}
