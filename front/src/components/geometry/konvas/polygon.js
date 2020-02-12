import Constant from "./../constants";

class Polygon {

    constructor(pol, width, height, stage, layer, el) {
        this.points = [];
        this.stage = stage;
        this.layer = layer;
        this.width = width;
        this.height = height;
        this.indexPol = parseInt(pol.indexPol);
        this.stroke = '#303030';
        this.type = 'Stresses';
        this.pol = pol;
        this.colors = [];

        pol.points.forEach(pnt => {
            this.points.push({
                x: pnt.x,
                y: pnt.y,
                index: pnt.index,
                color: '#ffffff'
            });
        });

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawPolygon(context, shape, this.points),
            stroke: this.stroke,
            strokeWidth: 1,
        });

        this.shape.listening(false);

        layer.add(this.shape);
    }

    getWidth() {
        return this.stage.width() - Constant.WIDTH_OFFSET;
    }

    getHeight() {
        return this.stage.height() - Constant.HEIGHT_OFFSET;
    }

    xTransform(x){
        let width = this.width;
        let widthContainer = this.getWidth();
        let xAxisOffset = Constant.X_OFFSET;
        return  ((x / width) * widthContainer) + xAxisOffset
    }

    yTransform(y){
        let height = this.height;
        let heightContainer = this.getHeight();
        let yAxisOffset = Constant.Y_OFFSET;
        return (((height - y) / height) * heightContainer) + yAxisOffset
    }

    drawPolygon(context, shape, points) {
        context.beginPath();
        context.moveTo(
            this.xTransform(points[0].x),
            this.yTransform(points[0].y),
        );
        points.forEach((pnt, index) => {
            if(index > 0) {
                context.lineTo(
                    this.xTransform(pnt.x),
                    this.yTransform(pnt.y)
                );
            }
        });
        context.closePath();

        // (!) Konva specific method, it is very important
        context.fillStrokeShape(shape);
    }

    getIndex() {
        return this.indexPol;
    }

    setType(type) {
        this.type = type;
    }

    getClientRect() {
        return this.shape.getClientRect(1);
    }

    setStroke(color){
        this.shape.stroke(color);
    }


    pointInsidePolygon(polygon, mousePoint, width, height) {
        let intersections = 0;
        for (let i = 0; i < polygon.points.length; i++) {

            let pntA = polygon.points[i];
            let pntB = polygon.points[(i + 1) % polygon.points.length];
            let xi = ((pntA.x / width) * this.getWidth()) + Constant.X_OFFSET,
                yi = (((height - pntA.y) / height) * this.getHeight()) + Constant.Y_OFFSET;
            let xj = ((pntB.x / width) * this.getWidth()) + Constant.X_OFFSET,
                yj = (((height - pntB.y) / height) * this.getHeight()) + Constant.Y_OFFSET;

            if (this.vectorIntersection(xi, yi, xj, yj, mousePoint[0], mousePoint[1], -1000, -1000)) {
                intersections += 1;
            }
        }

        return intersections % 2 !== 0;
    }


    vectorIntersection(a, b, c, d, p, q, r, s) {
        let det, gamma, lambda;

        det = (c - a) * (s - q) - (r - p) * (d - b);
        if (det === 0) {
            return false;
        } else {
            lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
            gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
            return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
        }
    }
}

export default Polygon;