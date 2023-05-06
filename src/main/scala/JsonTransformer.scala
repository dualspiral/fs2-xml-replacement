import cats.effect.IO
import cats.effect.unsafe.IORuntime
import fs2.data.json.{Token, render, tokens}
import fs2.io.file.Path.fromNioPath
import fs2.io.file.{Files => Fs2Files}
import fs2.{Pipe, Stream}

import java.nio.file.Path

object JsonTransformer {

  val ELEMENT: Seq[String] = "ROOT" :: "REPLACEMENT" :: Nil

  def loadString(json: String, replacement: String)(implicit ioRuntime: IORuntime): String =
    Stream.emit(json).through(transform(Map(ELEMENT -> replacement))).compile.string.unsafeRunSync()

  def loadFile(filePath: Path, replacement: String)(implicit ioRuntime: IORuntime): String =
    Fs2Files[IO].readUtf8Lines(fromNioPath(filePath)).through(transform(Map(ELEMENT -> replacement))).compile.string.unsafeRunSync()

  def transform(replacements: Map[Seq[String], String]): Pipe[IO, String, String] =
    _.through(tokens[IO, String])
      .mapAccumulate[Seq[String], Token](Nil)((currentLocation, event) => {
        event match {
          case Token.Key(value)                 => (currentLocation :+ value, event)
          case Token.StartArray                 => (currentLocation :+ "[]", event)
          case Token.EndObject | Token.EndArray => (currentLocation.dropRight(1), event)
          case Token.StartObject                => (currentLocation, event)
          case Token.StringValue(_) =>
            (currentLocation.dropRight(1), replacements.get(currentLocation).map[Token](str => Token.StringValue(str)).getOrElse(event))
          case Token.NullValue | Token.NumberValue(_) | Token.TrueValue | Token.FalseValue =>
            (currentLocation.dropRight(1), event)
        }
      })
      .map(_._2)
      .through(render.compact[IO])

}
