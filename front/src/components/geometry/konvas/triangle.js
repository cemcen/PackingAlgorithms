import Constant from "./../constants";

class Triangle {

    constructor(points, width, height, stage, layer) {
        this.points = points;
        this.stage = stage;
        this.layer = layer;
        this.width = width;
        this.height = height;
        this.fill = '#ffffff';
        this.stroke = '#ffffff';
        this.strokeWidth = 1;
        this.opacity = 1;


        this.centroid = [0,0];
        this.points.forEach(pnt => {
            this.centroid[0] += pnt.x;
            this.centroid[1] += pnt.y;
        });

        this.centroid[0] = this.centroid[0] / this.points.length;
        this.centroid[1] = this.centroid[1] / this.points.length;

        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawTriangle(context, shape, this.points),
            fill: this.fill,
            stroke: this.stroke,
            strokeWidth: this.strokeWidth,
            opacity: this.opacity,
            onFinish : function() {
                // remove all references from Konva
                this.shape.destroy();
            }
        });
        this.shape.listening(false);
        this.shape.hitStrokeWidth(0);
        this.shape.shadowForStrokeEnabled(false);
        this.shape.perfectDrawEnabled(false);

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

    fillTriangle(color) {
        this.fill = color;
        this.stroke = color;
        this.strokeWidth = 1;
        this.opacity = 0.5;
        this.noFill();
    }

    fillSelected() {
        if(this.opacity === 0.5) {
            this.shape.fill(this.fill);
            this.shape.stroke(this.stroke);
            this.shape.strokeWidth(0);
        } else {
            this.shape.fill('#cccccc');
            this.shape.stroke('#cccccc');
            this.shape.strokeWidth(1);
        }
        this.shape.opacity(this.opacity);
    }

    noFill() {
        this.shape.fill(this.fill);
        this.shape.stroke(this.stroke);
        this.shape.strokeWidth(this.strokeWidth);
        this.shape.opacity(1);
    }

    noProperties() {
        this.fill = '#ffffff';
        this.stroke = '#ffffff';
        this.strokeWidth = 0;
        this.opacity = 1;
        this.noFill();
    }

    destroy() {
        this.shape.destroy();
    }
}

export default Triangle;