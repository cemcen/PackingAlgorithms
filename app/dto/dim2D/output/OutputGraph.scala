package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class OutputGraph(nodes: List[Point2D], edges: List[(Point2D, Point2D)])

object OutputGraph {

  implicit object OutputGraphWrites extends Writes[OutputGraph] {

    def writes(u: OutputGraph): JsObject = Json.obj(
      "nodes"  -> u.nodes,
      "edges" -> u.edges
    )
  }

  implicit val read: Reads[OutputGraph] = Json.reads[OutputGraph]
  implicit val write: Writes[OutputGraph] = Json.writes[OutputGraph]
}