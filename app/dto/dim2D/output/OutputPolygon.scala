package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class OutputPolygon(points: List[Point2D])

object OutputPolygon {

  implicit object OutputPolygonWrites extends Writes[OutputPolygon] {

    def writes(u: OutputPolygon): JsObject = Json.obj(
      "points" -> u.points
    )
  }

  implicit val read: Reads[OutputPolygon] = Json.reads[OutputPolygon]
  implicit val write: Writes[OutputPolygon] = Json.writes[OutputPolygon]
}