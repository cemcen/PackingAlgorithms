import Constant from "./constants";
import Point from "./point";

class Polygon {

    constructor(pol, width, height, stage, layer) {
        this.points = [];
        this.stage = stage;
        this.layer = layer;
        this.width = width;
        this.height = height;
        pol.points.forEach(pnt => this.points.push({
                x: pnt.x,
                y: pnt.y
            }));

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawPolygon(context, shape, this.points),
            fill: '#ffffff',
            stroke: 'black',
            strokeWidth: 3
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
}

export default Polygon;