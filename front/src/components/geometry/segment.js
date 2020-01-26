import Constant from "./constants";
import Point from "./point";

class Segment {

    constructor(x1, y1, x2, y2, width, height, key, properties, indexPol) {
        this.pntA = new Point(x1, y1, width, height);
        this.pntB = new Point(x2, y2, width, height);
        this.mouseOver = false;
        this.selected = false;
        this.key = key;
        this.properties = properties;
        this.indexPol = indexPol;
    }

    draw(p){
        p.line(
            this.pntA.xTransform(this.pntA.x, p),
            this.pntA.yTransform(this.pntA.y, p),
            this.pntB.xTransform(this.pntB.x, p),
            this.pntB.yTransform(this.pntB.y, p)
        );
    }

    checkMouseOver(p, propertiesDict) {
        p.stroke(30, 30, 30);
        if(this.properties && this.properties.length > 0 && this.properties[0].key in propertiesDict) {
            p.stroke(propertiesDict[this.properties[0].key].color);
        }
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

    contains(pnt) {
        return (Math.abs(pnt.x - this.pntA.x) < 1e-8 && Math.abs(pnt.y - this.pntA.y) < 1e-8) ||  (Math.abs(pnt.x - this.pntB.x) < 1e-8 && Math.abs(pnt.y - this.pntB.y) < 1e-8);
    }

    isSelected() {
        return this.selected;
    }

    getKey() {
        return this.key;
    }

    getPolygonIndex() {
        return this.indexPol;
    }
}

export default Segment;