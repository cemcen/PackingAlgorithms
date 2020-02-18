import Constant from "./../constants";

class Point {

    constructor(x, y, width, height, layer, stage, properties) {
        this.point = [x, y];
        this.selected = false;
        this.properties = properties;
        this.stroke = '#202020';
        this.strokeWidth = 3;
        this.layer = layer;
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.radius = 4;


        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawCircle(context, shape, this.point),
            fill: 'black',
            stroke: this.stroke,
            strokeWidth: this.strokeWidth
        });

        this.shape.listening(false);
        layer.add(this.shape);
    }

    drawCircle(context, shape, point) {
        context.beginPath();
        context.arc(this.xTransform(point[0]), this.yTransform(point[1]), this.radius, 0, Math.PI * 2, false);
        context.closePath();

        // (!) Konva specific method, it is very important
        context.fillStrokeShape(shape);
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

    isSelected() {
        return this.selected;
    }

    mouseClick(mouse) {

    }

    dragBoxIntersect(rect) {
        this.selected = false;
        this.shape.fill('black');
        this.shape.stroke('#202020');

        let intersections = 0;
        let x = this.xTransform(this.point[0]);
        let y = this.yTransform(this.point[1]);

        let rectArray = [
            [rect.x(), rect.y()],
            [rect.x(), rect.y() + rect.height()],
            [rect.x() + rect.width(), rect.y() + rect.height()],
            [rect.x() + rect.width(), rect.y()],
        ];

        rectArray.forEach((pnt, i) => {
            let pntA = rectArray[i];
            let pntB = rectArray[(i + 1) % rectArray.length];

            if(this.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x, y, -1000, -1000)) {
                intersections += 1;
            }
        });

        if(intersections % 2 !== 0) {
            this.selected = true;
            this.shape.fill('teal');
            this.shape.stroke('teal');
        }

        this.shape.draw();
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

export default Point;