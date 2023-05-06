import cats.effect.unsafe.implicits.global
import io.circe.parser._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class JsonTransformerSpec extends AnyFreeSpec with Matchers {

  "XML with LRN must have a replacement" in {
    val json =
      """
        |{ "ROOT": { "abc": "def", "REPLACEMENT": "r" } }
        |""".stripMargin

    val target = {
      parse(
        """
          |{ "ROOT": { "abc": "def", "REPLACEMENT": "rr" } }
          |""".stripMargin
      )
    }

    parse(JsonTransformer.loadString(json, "rr")) mustBe target
  }

  "XML without replacement must not be replaced" in {
    val json =
      """
        |{ "ROOT": { "abc": "def", "no": "r" } }
        |""".stripMargin

    parse(JsonTransformer.loadString(json, "rr")) mustBe parse(json)
  }

  "XML with wrong root but an LRN must not be replaced" in {
    val json =
      """
        |{ "ROOT2": { "abc": "def", "REPLACEMENT": "r" } }
        |""".stripMargin

    parse(JsonTransformer.loadString(json, "rr")) mustBe parse(json)
  }

}
