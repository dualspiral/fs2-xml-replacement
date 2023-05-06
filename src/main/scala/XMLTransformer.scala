import cats.effect.IO
import cats.effect.unsafe.IORuntime
import fs2.{Pipe, Stream}
import fs2.data.xml.XmlEvent.XmlString
import fs2.data.xml.{XmlEvent, events, render}
import fs2.io.file.{Files => Fs2Files}
import fs2.io.file.Path.fromNioPath

import java.nio.file.Path

object XMLTransformer {

  val ELEMENT: Seq[String] = "ROOT" :: "REPLACEMENT" :: Nil

  def loadString(xml: String, replacement: String)(implicit ioRuntime: IORuntime): String =
    Stream.emit(xml).through(transform(Map(ELEMENT -> replacement))).compile.string.unsafeRunSync()

  def loadFile(filePath: Path, replacement: String)(implicit ioRuntime: IORuntime): String =
    Fs2Files[IO].readUtf8Lines(fromNioPath(filePath)).through(transform(Map(ELEMENT -> replacement))).compile.string.unsafeRunSync()

  def transform(replacements: Map[Seq[String], String]): Pipe[IO, String, String] =
    _.through(events[IO, String]())
      .mapAccumulate[Seq[String], XmlEvent](Nil)((currentLocation, event) => {
        event match {
          case XmlEvent.XmlString(x, _) =>
            (currentLocation, replacements.get(currentLocation).map[XmlEvent](str => XmlString(str, isCDATA = false)).getOrElse(event))
          case XmlEvent.StartTag(tag, _, _) => (currentLocation :+ tag.local, event)
          case _: XmlEvent.EndTag           => (currentLocation.dropRight(1), event)
          case _                            => (currentLocation, event)
        }
      })
      .map(_._2)
      .through(render[IO](collapseEmpty = true))

}
