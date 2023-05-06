import cats.effect.unsafe.implicits.global
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.xml.XML

class XMLTransformerSpec extends AnyFreeSpec with Matchers {

  "XML with LRN must have a replacement" in {
    val xml =
      <ROOT>
        <abc>def</abc>
        <REPLACEMENT>r</REPLACEMENT>
      </ROOT>.mkString

    val target =
      <ROOT>
        <abc>def</abc>
        <REPLACEMENT>rr</REPLACEMENT>
      </ROOT>

    XML.loadString(XMLTransformer.loadString(xml, "rr")) mustBe target
  }

  "XML with LRN and namespace must have a replacement" in {
    val xml =
      <test:ROOT>
        <abc>def</abc>
        <REPLACEMENT>r</REPLACEMENT>
      </test:ROOT>.mkString

    val target =
      <test:ROOT>
        <abc>def</abc>
        <REPLACEMENT>rr</REPLACEMENT>
      </test:ROOT>

    XML.loadString(XMLTransformer.loadString(xml, "rr")) mustBe target
  }

  "XML without replacement must not be replaced" in {
    val xml =
      <ROOT>
        <abc>def</abc>
        <no>r</no>
      </ROOT>

    XML.loadString(XMLTransformer.loadString(xml.mkString, "rr")) mustBe xml
  }

  "XML with wrong root but an LRN must not be replaced" in {
    val xml =
      <ROOT2>
        <abc>def</abc>
        <REPLACEMENT>r</REPLACEMENT>
      </ROOT2>

    XML.loadString(XMLTransformer.loadString(xml.mkString, "rr")) mustBe xml
  }

}
