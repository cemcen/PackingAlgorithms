import Constant from "./constants";

class PaintTriangles {

    constructor(points, width, height, stage, layer) {
        this.points = points;
        this.stage = stage;
        this.layer = layer;
        this.width = width;
        this.height = height;
        this.fill = '#ffffff';
        this.stroke = '#303030';


        this.centroid = [0,0];
        this.points.forEach(pnt => {
            this.centroid[0] += pnt[0];
            this.centroid[1] += pnt[1];
        });

        this.centroid[0] = this.centroid[0] / this.points.length;
        this.centroid[1] = this.centroid[1] / this.points.length;

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawTriangle(context, shape, this.points),
            fill: this.fill,
            stroke: 'rgba(0,0,0,0)',
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

    drawTriangle(context, shape, points) {
        context.beginPath();
        context.moveTo(
            this.xTransform(points[0][0]),
            this.yTransform(points[0][1]),
        );
        points.forEach((pnt, index) => {
            if(index > 0) {
                context.lineTo(
                    this.xTransform(pnt[0]),
                    this.yTransform(pnt[1])
                );
            }
        });
        context.closePath();

        // (!) Konva specific method, it is very important
        context.fillStrokeShape(shape);
    }

    setTriangleColor(points, vertices) {

        let minimumDistance = Number.MAX_VALUE;
        let color = null;
        let index = null;
        points.forEach((pnt, idx) => {
            let minDist = Math.pow(pnt.x - this.centroid[0], 2) + Math.pow(pnt.y - this.centroid[1], 2);
            if(minimumDistance > minDist) {
                minimumDistance = minDist;
                index = idx;
                color = vertices[pnt.index - 1].color;
            }
        });
        points.forEach((pnt, idx) => {
            if(index !== idx) {
                let dist = Math.pow(pnt.x - this.centroid[0], 2) + Math.pow(pnt.y - this.centroid[1], 2);
                let weight = minimumDistance / dist;
                color = color * (1 - weight) + vertices[pnt.index - 1].color * weight;
            }
        });

        this.shape.fill('rgb(' + this.HSVtoRGB(color/ 360, 1, 1) + ')');
        this.shape.stroke('rgb(' + this.HSVtoRGB(color / 360, 1, 1) + ')');
        this.shape.draw();
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

export default PaintTriangles;