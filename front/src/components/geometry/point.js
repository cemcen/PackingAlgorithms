import Constant from "./constants";

class Point {

    constructor(x, y, width, height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mouseOver = false;
        this.selected = false;
    }

    draw(p){
        p.point(this.xTransform(this.x, p), this.yTransform(this.y, p));
    }

    mousePressed(p) {
        this.mouseOver = (this.checkIfMouseOverPoint(p));
        this.selected = this.mouseOver || (this.selected && p.keyIsDown(p.OPTION));
    }

    checkMouseOver(p) {
        p.stroke(30, 30, 30);
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
}

export default Point;