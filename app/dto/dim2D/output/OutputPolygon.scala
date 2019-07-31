package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class OutputPolygon(points: List[Point2D], label: String, radius: Double, hole: Boolean)

object OutputPolygon {

  implicit object OutputPolygonWrites extends Writes[OutputPolygon] {

    def writes(u: OutputPolygon): JsObject = Json.obj(
      "label"  -> u.label,
      "radius" -> u.radius,
      "points" -> u.points,
      "hole" -> u.hole,
    )
  }

  implicit val read: Reads[OutputPolygon] = Json.reads[OutputPolygon]
  implicit val write: Writes[OutputPolygon] = Json.writes[OutputPolygon]
}