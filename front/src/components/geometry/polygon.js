import Constant from "./constants";
import Point from "./point";
import Delaunator from 'delaunator';
import PaintTriangles from "./paintTriangles";

class Polygon {

    constructor(pol, width, height, stage, layer) {
        this.points = [];
        this.stage = stage;
        this.layer = layer;
        this.width = width;
        this.height = height;
        this.indexPol = parseInt(pol.indexPol);
        this.fill = '#ffffff';
        this.stroke = '#303030';
        this.type = 'Stresses';
        this.pol = pol;
        this.colors = [];
        this.triangles = [];

        // let pointsP = [];
        // let maxY = 0, maxX = 0;
        // // let minX = Number.MAX_VALUE, minY = Number.MAX_VALUE;
        pol.points.forEach(pnt => {
        //     pointsP.push([pnt.x, pnt.y]);
            this.points.push({
                x: pnt.x,
                y: pnt.y,
                index: pnt.index,
                color: '#ffffff'
            });
        //     if(minX > pnt.x) minX = pnt.x;
        //     if(minY > pnt.y) minY = pnt.y;
        //     if(maxX < pnt.x) maxX = pnt.x;
        //     if(maxY < pnt.y) maxY = pnt.y;
        });
        //
        // let numberOfRandomPointsAdded = 4;
        // for(let i = 0; i < numberOfRandomPointsAdded; i++) {
        //     let inside = false;
        //     let pnt = [];
        //     while(!inside) {
        //         let randomX = Math.random();
        //         let randomY = Math.random();
        //         pnt = [randomX*(maxX-minX)+minX, randomY*(maxY-minY)+minY];
        //         inside = this.pointInsidePolygon(this.pol, [this.xTransform(pnt[0]), this.yTransform(pnt[1])], this.width, this.height);
        //     }
        //
        //     pointsP.push(pnt);
        // }
        //
        // const delaunay = Delaunator.from(pointsP);
        // for (let i = 0; i < delaunay.triangles.length; i += 3) {
        //     let pointsT =  [pointsP[delaunay.triangles[i]], pointsP[delaunay.triangles[i + 1]], pointsP[delaunay.triangles[i + 2]]];
        //     this.triangles.push(new PaintTriangles(pointsT, this.width, this.height, this.stage, this.layer));
        // }

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawPolygon(context, shape, this.points),
            fill: 'rgba(255,255,255,0)',
            stroke: this.stroke,
            strokeWidth: 1
        });

        this.shape.on('click', () => {
           //console.log(this.pol, this.indexPol, this.value, this.shape.fill());
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
        let rgbColor = this.HSVtoRGB(hsl/360, 1, 1);
        this.shape.fill('rgb(' + rgbColor + ')');
        this.shape.stroke('rgb(' + rgbColor + ')');
        this.shape.filters([]);
        this.shape.draw();
    }

    setVertexColors(vertices) {
        this.colors = [this.HSVtoRGB((vertices[this.points[0].index - 1].color)/360, 1, 1)];
        let color = this.HSVtoRGB((vertices[this.points[0].index - 1].color)/360, 1, 1);
        this.points.forEach((pnt, idx) => {
            if(idx > 0) {
                let nColor = this.HSVtoRGB((vertices[pnt.index - 1].color) / 360, 1, 1);
                this.colors.push(nColor);
                color = [(color[0] + nColor[0]) / 2, (color[1] + nColor[1]) / 2, (color[2] + nColor[2]) / 2]
            }
        });
        this.shape.fill('rgb(' + color + ')');
        this.shape.stroke('rgb(' + color + ')');
        this.shape.draw();
        // this.triangles.forEach(triang => {
        //     triang.setTriangleColor(this.points, vertices);
        // });
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