package org.w3.banana.n3js

import org.w3.banana._
import zcheck.SpecLite
import scala.scalajs.js

object N3Test extends SpecLite {

  // see https://github.com/RubenVerborgh/N3.js#from-rdf-chunks-to-triples
  "N3.Parser() -- chunks" in {

    val parser = N3.Parser()

    var triples: Vector[Triple] = Vector.empty

    parser.parse(
      (error: js.Any, triple: js.UndefOr[Triple], prefixes: js.UndefOr[js.Any]) => {
        triple.foreach { (t: Triple) => triples :+= t }
        prefixes.foreach { p => println("prefixes: "+p) }
      }
    )

    parser.addChunk("@prefix c: <http://example.org/cartoons#>.\n")
    parser.addChunk("c:Tom a ")
    parser.addChunk("c:Cat. c:Jerry a")
    // got an exception when adding that...
//    parser.addChunk(" c:Mouse.")
    parser.end()

    check(triples.size == 1)

    val triple = triples.head

    check(triple.subject == "http://example.org/cartoons#Tom")
    check(triple.predicate == "http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
    check(triple.`object` == "http://example.org/cartoons#Cat")

  }

  // see https://github.com/RubenVerborgh/N3.js#storing
  "N3.Store()" in {

    val store = N3.Store()

    store.addTriple("http://ex.org/Pluto",  "http://ex.org/type", "http://ex.org/Dog")
    store.addTriple("http://ex.org/Mickey", "http://ex.org/type", "http://ex.org/Mouse")

    val results = store.find("http://ex.org/Pluto", null, null)

    check(results.size == 1)

    val result = results(0)

    check(result.subject == "http://ex.org/Pluto")
    check(result.predicate == "http://ex.org/type")
    check(result.`object` == "http://ex.org/Dog")

    check(store.find(null, null, null).size == 2)

  }

  // see https://github.com/RubenVerborgh/N3.js#utility
  "N3.Util" in {

    val N3Util = N3.Util

    check(N3Util.isIRI("http://example.org/cartoons#Mickey"))

    check(N3Util.isLiteral(""""Mickey Mouse""""))
    check(N3Util.getLiteralValue(""""Mickey Mouse"""") == "Mickey Mouse")
    check(N3Util.isLiteral(""""Mickey Mouse"@en"""))
    check(N3Util.getLiteralLanguage(""""Mickey Mouse"@en""") == "en")
    check(N3Util.isLiteral(""""3"^^http://www.w3.org/2001/XMLSchema#integer"""))
    check(N3Util.getLiteralType(""""3"^^http://www.w3.org/2001/XMLSchema#integer""") == "http://www.w3.org/2001/XMLSchema#integer")
    check(N3Util.isLiteral(""""http://example.org/""""))
    check(N3Util.getLiteralValue(""""http://example.org/"""") == "http://example.org/")

    check(N3Util.isLiteral(""""This word is "quoted"!""""))
    check(N3Util.isLiteral(""""3"^^http://www.w3.org/2001/XMLSchema#integer"""))

    check(N3Util.isBlank("_:b1"))
    check(! N3Util.isIRI("_:b1"))
    check(! N3Util.isLiteral("_:b1"))

    val prefixes = js.JSON.parse("""{ "rdfs": "http://www.w3.org/2000/01/rdf-schema#" }""")
    check(N3Util.isPrefixedName("rdfs:label"))
    check(N3Util.expandPrefixedName("rdfs:label", prefixes) == "http://www.w3.org/2000/01/rdf-schema#label")

  }


}
