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
        this.selected = false;
        this.polygonCentroid = {x: 0, y: 0};

        pol.points.forEach(pnt => {
            this.points.push({
                x: pnt.x,
                y: pnt.y,
                index: pnt.index,
                color: '#ffffff'
            });
            this.polygonCentroid.x += pnt.x;
            this.polygonCentroid.y += pnt.y;
        });

        this.polygonCentroid.x = this.polygonCentroid.x / pol.points.length;
        this.polygonCentroid.y = this.polygonCentroid.y / pol.points.length;


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


    mouseClick(mouse) {
        if(this.pointInsidePolygon(this.points, mouse)) {
            this.selected = true;
            this.shape.fill('#cccccc');
        } else {
            this.selected = false;
            this.shape.fill('#ffffff');
        }
        this.shape.draw();
    }


    dragBoxIntersect(rect) {
        this.selected = false;
        this.shape.fill('#ffffff');
        let rectArray = [
            {x: rect.x(), y: rect.y()},
            {x: rect.x(), y: rect.y() + rect.height()},
            {x: rect.x() + rect.width(), y: rect.y() + rect.height()},
            {x: rect.x() + rect.width(), y: rect.y()},
        ];
        if(this.pointInsidePolygonV2(rectArray, [this.xTransform(this.polygonCentroid.x), this.yTransform(this.polygonCentroid.y)])) {
            this.selected = true;
            this.shape.fill('#cccccc');
        } else {
            for (let i = 0; i < this.points.length; i++) {

                let pntA = this.points[i];
                let pntB = this.points[(i + 1) % this.points.length];
                let xi = this.xTransform(pntA.x),
                    yi = this.yTransform(pntA.y);
                let xj = this.xTransform(pntB.x),
                    yj = this.yTransform(pntB.y);

                for (let j = 0; j < rectArray.length; j++) {
                    let pntC = rectArray[j];
                    let pntD = rectArray[(j + 1) % rectArray.length];

                    if (this.vectorIntersection(xi, yi, xj, yj, pntC.x, pntC.y, pntD.x, pntD.y)) {
                        this.selected = true;
                        this.shape.fill('#cccccc');
                        this.shape.draw();
                        return;
                    }
                }


            }
        }
        this.shape.draw();
    }

    pointInsidePolygonV2(points, mousePoint) {
        let intersections = 0;
        for (let i = 0; i < points.length; i++) {

            let pntA = points[i];
            let pntB = points[(i + 1) % points.length];
            let xi = pntA.x,
                yi = pntA.y;
            let xj = pntB.x,
                yj = pntB.y;

            if (this.vectorIntersection(xi, yi, xj, yj, mousePoint[0], mousePoint[1], -1000, -1000)) {
                intersections += 1;
            }
        }

        return intersections % 2 !== 0;
    }



    pointInsidePolygon(points, mousePoint) {
        let intersections = 0;
        for (let i = 0; i < points.length; i++) {

            let pntA = points[i];
            let pntB = points[(i + 1) % points.length];
            let xi = this.xTransform(pntA.x),
                yi = this.yTransform(pntA.y);
            let xj = this.xTransform(pntB.x),
                yj = this.yTransform(pntB.y);

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