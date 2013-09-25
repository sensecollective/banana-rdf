package org.w3.banana.syntax

import org.w3.banana._

trait LiteralSyntax[Rdf <: RDF] { self: Syntax[Rdf] =>

  implicit def literalW(literal: Rdf#Literal) =
    new LiteralW[Rdf](literal)

}

class LiteralW[Rdf <: RDF](val literal: Rdf#Literal) extends AnyVal {

  def fold[T](funTL: Rdf#TypedLiteral => T, funLL: Rdf#LangLiteral => T)(implicit ops: RDFOps[Rdf]): T =
    ops.foldLiteral(literal)(funTL, funLL)

  def lexicalForm(implicit ops: RDFOps[Rdf]): String =
    ops.foldLiteral(literal)(
      tl => ops.fromTypedLiteral(tl)._1,
      ll => ops.fromLangLiteral(ll)._1
    )

}
