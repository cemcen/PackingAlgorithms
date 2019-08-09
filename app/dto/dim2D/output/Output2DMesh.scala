package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class Output2DMesh(polygons: List[OutputPolygon], graph: OutputGraph, width: Double, height: Double)

object Output2DMesh {

  implicit object Output2DMeshWrites extends Writes[Output2DMesh] {

    def writes(u: Output2DMesh): JsObject = Json.obj(
      "polygons" -> u.polygons,
      "graph" -> u.graph,
      "width" -> u.width,
      "height" -> u.height
    )
  }

  implicit val read: Reads[Output2DMesh] = Json.reads[Output2DMesh]
  implicit val write: Writes[Output2DMesh] = Json.writes[Output2DMesh]
}