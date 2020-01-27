import Constant from "./constants";

class Point {

    constructor(x, y, width, height, key, properties) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mouseOver = false;
        this.selected = false;
        this.key = key;
        this.properties = properties;
    }

    draw(p){
        p.point(this.xTransform(this.x, p), this.yTransform(this.y, p));
    }

    mousePressed(p) {
        this.mouseOver = (this.checkIfMouseOverPoint(p));
        this.selected = this.mouseOver || (this.selected && p.keyIsDown(p.OPTION));
    }

    checkMouseOver(p, propertiesDict) {
        p.stroke(30, 30, 30);
        if(this.properties && this.properties.length > 0 && this.properties[0].key in propertiesDict) {
            p.stroke(propertiesDict[this.properties[0].key].color);
        }
        p.strokeWeight(10);
        if(this.selected || this.checkIfMouseOverPoint(p) ) {
            p.stroke('teal');
            p.strokeWeight(20);
            this.mouseOver = true;
        } else {
            this.mouseOver = false;
        }
    }

    checkIfMouseOverPoint(p){
        return (Math.abs(p.mouseX - this.xTransform(this.x, p)) <= 4
            && Math.abs(p.mouseY - this.yTransform(this.y, p)) <= 4);
    }

    isInsideBox(p, bx, by, xInit, yInit) {
        let intersections = 0;
        let x = this.xTransform(this.x, p);
        let y = this.yTransform(this.y, p);
        let points = [
            [bx, by],
            [bx, yInit],
            [xInit, yInit],
            [xInit, by]
        ];

        points.forEach((pnt, i) => {
            let pntA = points[i];
            let pntB = points[(i + 1) % points.length];

            if(this.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x, y, -1000, -1000)) {
                intersections += 1;
            }
        });

        if(intersections % 2 !== 0) {
            this.selected = true;
        }
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

    static getWidth(p) {
        return p.width - Constant.WIDTH_OFFSET;
    }

    static getHeight(p) {
        return p.height - Constant.HEIGHT_OFFSET;
    }

    static getOffsetXAxis() {
        return Constant.X_OFFSET;
    }

    static getOffsetYAxis() {
        return Constant.Y_OFFSET;
    }

    xTransform(x, p){
        let width = this.width;
        let widthContainer = Point.getWidth(p);
        let xAxisOffset = Point.getOffsetXAxis();
        return  ((x / width) * widthContainer) + xAxisOffset
    }

    yTransform(y, p){
        let height = this.height;
        let heightContainer = Point.getHeight(p);
        let yAxisOffset = Point.getOffsetYAxis();
        return (((height - y) / height) * heightContainer) + yAxisOffset
    }

    isSelected() {
        return this.selected;
    }

    getKey() {
        return this.key;
    }
}

export default Point;