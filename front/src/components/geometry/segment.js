import Constant from "./constants";
import Point from "./point";

class Segment {

    constructor(x1, y1, x2, y2, width, height) {
        this.pntA = new Point(x1, y1, width, height);
        this.pntB = new Point(x2, y2, width, height);
        this.mouseOver = false;
        this.selected = false;
    }

    draw(p){
        p.line(
            this.pntA.xTransform(this.pntA.x, p),
            this.pntA.yTransform(this.pntA.y, p),
            this.pntB.xTransform(this.pntB.x, p),
            this.pntB.yTransform(this.pntB.y, p)
        );
    }

    checkMouseOver(p) {
        p.stroke(30, 30, 30);
        p.strokeWeight(3);
        if( this.selected || this.checkPointInsideSegment(p)) {
            p.stroke('teal');
            p.strokeWeight(5);
            this.mouseOver = true;
        } else {
            this.mouseOver = false;
        }
    }

    checkPointInsideSegment(p) {
        let pntAx = this.pntA.xTransform(this.pntA.x, p);
        let pntAy = this.pntA.yTransform(this.pntA.y, p);
        let pntBx = this.pntB.xTransform(this.pntB.x, p);
        let pntBy = this.pntB.yTransform(this.pntB.y, p);
        return (Math.abs(p.mouseY - pntAy) < 4 && p.mouseX >= Math.min(pntAx, pntBx) && p.mouseX <= Math.max(pntAx, pntBx))
            || (Math.abs(p.mouseX - pntAx) < 4 && p.mouseY >= Math.min(pntAy, pntBy) && p.mouseY <= Math.max(pntAy, pntBy));
    }

    mousePressed(p) {
        this.mouseOver = (this.checkPointInsideSegment(p));
        this.selected = this.mouseOver || (this.selected && p.keyIsDown(p.OPTION));
    }
}

export default Segment;