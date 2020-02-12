import Constant from "./constants";

class Polygon {

    constructor(pol, width, height, stage, layer) {
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

        this.shape.on('click', () => {
           console.log(this.pol, this.indexPol, this.value, this.hsl, this.vertices, this.pntMax, this.pntMin, this.points);
        });

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

    setColor(value, hsl) {
        this.value = value;
        this.hsl = hsl;
        let rgbColor = this.HSVtoRGB(hsl/360, 1, 1);
        this.shape.fill('rgb(' + rgbColor + ')');
        this.shape.stroke('rgb(' + rgbColor + ')');
        this.shape.draw();
    }

    setVertexColors(vertices) {
        this.vertices = [];
        let pntMin = this.points[0];
        let pntMax = this.points[0];
        let maxValue = vertices[this.points[0].index - 1].color;
        let minValue = vertices[this.points[0].index - 1].color;
        this.points.forEach((pnt, idx) => {
            this.vertices.push(vertices[pnt.index - 1]);
            if(idx > 0) {

                if(minValue > vertices[pnt.index - 1].color) {
                    minValue = vertices[pnt.index - 1].color;
                    pntMin = pnt;
                }

                if(maxValue < vertices[pnt.index - 1].color) {
                    maxValue = vertices[pnt.index - 1].color;
                    pntMax = pnt;
                }
            }

        });

        this.shape.fill(null);
        this.shape.fillLinearGradientStartPoint({ x: this.xTransform(pntMin.x), y: this.yTransform(pntMin.y) });
        this.shape.fillLinearGradientEndPoint({ x: this.xTransform(pntMax.x), y: this.yTransform(pntMax.y) });
        this.shape.fillLinearGradientColorStops(
            [0, 'rgb(' + this.HSVtoRGB((vertices[pntMin.index - 1].color)/360, 1, 1) + ')',
                1, 'rgb(' + this.HSVtoRGB((vertices[pntMax.index - 1].color)/360, 1, 1) + ')']);
        this.shape.stroke('rgb(0,0,0,0)');
        this.shape.strokeWidth(null);
        this.shape.draw();
    }

    paintLinearGradient(e) {
        console.log(e);
    }

    HSVtoRGB(h, s, v) {
        var r, g, b, i, f, p, q, t;
        if (arguments.length === 1) {
            s = h.s, v = h.v, h = h.h;
        }
        i = Math.floor(h * 6);
        f = h * 6 - i;
        p = v * (1 - s);
        q = v * (1 - f * s);
        t = v * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0: r = v, g = t, b = p; break;
            case 1: r = q, g = v, b = p; break;
            case 2: r = p, g = v, b = t; break;
            case 3: r = p, g = q, b = v; break;
            case 4: r = t, g = p, b = v; break;
            case 5: r = v, g = p, b = q; break;
        }
        return [ Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
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