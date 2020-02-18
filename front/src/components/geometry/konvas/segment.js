import Constant from "./../constants";

class Segment {

    constructor(x1, y1, x2, y2, width, height, layer, stage, properties, indexPol) {
        this.line = [x1, y1, x2, y2];
        this.selected = false;
        this.properties = properties;
        this.stroke = '#202020';
        this.strokeWidth = 3;
        this.indexPol = indexPol;
        this.layer = layer;
        this.stage = stage;
        this.width = width;
        this.height = height;


        this.shape = new Konva.Shape({
            sceneFunc: (context, shape) => this.drawLine(context, shape, this.line),
            stroke: this.stroke,
            strokeWidth: this.strokeWidth,
        });

        this.shape.listening(false);
        layer.add(this.shape);
    }

    drawLine(context, shape, line) {
        context.beginPath();
        context.moveTo(
            this.xTransform(line[0]),
            this.yTransform(line[1]),
        );
        context.lineTo(
            this.xTransform(line[2]),
            this.yTransform(line[3])
        );
        context.closePath();

        // (!) Konva specific method, it is very important
        context.fillStrokeShape(shape);
    }

    mouseClick(mouse) {
        /*if(this.contains(mouse)) {
            this.selected = true;
            this.shape.stroke('teal');
        } else {
            this.selected = false;
            this.shape.stroke('#202020');
        }
        this.shape.draw();*/
    }

    contains(pnt) {
        return (Math.abs(pnt[0] - this.xTransform(this.line[0])) < 5
            && Math.abs(pnt[1] - this.yTransform(this.line[1])) < 5)
            ||  (Math.abs(pnt[0] - this.xTransform(this.line[2])) < 5
                && Math.abs(pnt[1] - this.yTransform(this.line[3])) < 5);
    }


    dragBoxIntersect(rect) {
        this.selected = false;
        this.shape.stroke('#202020');

        let intersections1 = 0;
        let intersections2 = 0;
        let x1 = this.xTransform(this.line[0]);
        let y1 = this.yTransform(this.line[1]);
        let x2 = this.xTransform(this.line[2]);
        let y2 = this.yTransform(this.line[3]);

        let rectArray = [
            [rect.x(), rect.y()],
            [rect.x(), rect.y() + rect.height()],
            [rect.x() + rect.width(), rect.y() + rect.height()],
            [rect.x() + rect.width(), rect.y()],
        ];

        rectArray.forEach((pnt, i) => {
            let pntA = rectArray[i];
            let pntB = rectArray[(i + 1) % rectArray.length];

            if(this.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x1, y1, -1000, -1000)) {
                intersections1 += 1;
            }
            if(this.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x2, y2, -1000, -1000)) {
                intersections2 += 1;
            }
        });

        if(intersections1 % 2 !== 0 || intersections2 % 2 !== 0) {
            this.selected = true;
            this.shape.stroke('teal');
        }
        this.shape.draw();
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

    getPolygonIndex() {
        return this.indexPol;
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

export default Segment;