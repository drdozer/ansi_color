package uk.co.turingatemyhamster.colors

import scala.util.parsing.combinator.RegexParsers

/**
 * Created by nmrp3 on 22/12/13.
 */
object FormattedLiterals {

  implicit class FormattedHelper(val sc: StringContext) extends AnyVal {
    def ansi(args: Any*): String = {
      val p = FLParser.parseAll(FLParser.content, sc.standardInterpolator(identity, args))
      val sb = new StringBuilder
      p.get.format(Codes(), sb)
      sb.toString()
    }
  }

  object FLParser extends RegexParsers {
    override def skipWhitespace = false

    val BACKSLASH: Parser[String] = """\"""
    val L_BRACKET: Parser[String] = """{"""
    val R_BRACKET: Parser[String] = """}"""

    lazy val escapedBS: Parser[StringExp] = BACKSLASH ~ BACKSLASH ^^
      { case a ~ b => StringExp(a + b) }

    lazy val raw_rB: Parser[StringExp] = R_BRACKET ^^ (StringExp apply _)

    lazy val word: Parser[String] = """\w+"""r

    lazy val command: Parser[CommandExp] = BACKSLASH ~ word ~ L_BRACKET ~ content ~ R_BRACKET ^^
      { case _ ~ w ~ _ ~ c ~ _ => CommandExp(w, c) }

    lazy val raw_text: Parser[String] = """[^\\}]+"""r

    lazy val text: Parser[StringExp] = raw_text ^^ (StringExp apply _)

    lazy val content: Parser[FLExp] = (command | escapedBS | text).* ^^ (CompoundExp apply _)
  }

  sealed trait FLExp {
    def format(c: Codes, sb: StringBuilder)
  }

  case class StringExp(content: String) extends FLExp {
    def format(c: Codes, sb: StringBuilder) {
      sb append content
    }
  }

  val cCodes = Map(
    "black" -> 0,
    "red" -> 1,
    "green" -> 2,
    "yellow" -> 3,
    "blue" -> 4,
    "magenta" -> 5,
    "cyan" -> 6,
    "white" -> 7)

  case class CommandExp(command: String, content: FLExp) extends FLExp {
    def format(c: Codes, sb: StringBuilder) {
      command match {
        case "bold" =>
          sb append "\u001b[1m"
          content.format(c.copy(bold = true), sb)
          sb append "\u001b[22m"
        case "italic" =>
          sb append "\u001b[3m"
          content.format(c.copy(italic = true), sb)
          sb append "\u001b[23m"
        case "Underline" =>
          sb append "\u001b[4m"
          content.format(c.copy(underline = true), sb)
          sb append "\u001b[24m"
        case "blink" =>
          sb append "\u001b[5m"
          content.format(c.copy(blink = true), sb)
          sb append "\u001b[25m"
        case "reverse" =>
          sb append "\u001b[7m"
          content.format(c.copy(blink = true), sb)
          sb append "\u001b[27m"
        case color =>
          val cCode = cCodes(color)
          sb append s"\u001b[3${cCode}m"
          content.format(c.copy(color = cCode), sb)
          sb append s"\u001b[3${c.color}m"
      }
    }
  }

  case class CompoundExp(children: Seq[FLExp]) extends FLExp {
    def format(c: Codes, sb: StringBuilder) {
      for(ch <- children) ch.format(c, sb)
    }
  }

  case class Codes(bold: Boolean = false,
                   italic: Boolean = false,
                   underline: Boolean = false,
                   blink: Boolean = false,
                   color: Integer = 9)
}
