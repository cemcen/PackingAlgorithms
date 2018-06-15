package dto.dim2D.output

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class Point2D(x: Int, y: Int)

object Point2D {

  implicit object Point2DWrites extends Writes[Point2D] {

    def writes(u: Point2D): JsObject = Json.obj(
      "x" -> u.x,
      "y" -> u.y
    )
  }

  implicit val read: Reads[Point2D] = Json.reads[Point2D]
  implicit val write: Writes[Point2D] = Json.writes[Point2D]
}