import Constant from "./constants";
import Point from "./point";
import Delaunator from 'delaunator';

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

        let pointsP = [];
        pol.points.forEach(pnt => {
            pointsP.push([pnt.x, pnt.y]);
            this.points.push({
                x: pnt.x,
                y: pnt.y,
                index: pnt.index,
                color: '#ffffff'
            })
        });

        let numberOfRandomPointsAdded = 100;

        const delaunay = Delaunator.from(pointsP);
        console.log(delaunay.triangles);

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawPolygon(context, shape, this.points),
            fill: this.fill,
            stroke: this.stroke,
            filters: [ Konva.Filters.Pixelate ],
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
}

export default Polygon;