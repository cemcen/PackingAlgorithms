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

    checkIntersectionWithBox(p, bx, by, xInit, yInit) {
        let intersections1 = 0;
        let intersections2 = 0;
        let x1 = this.pntA.xTransform(this.pntA.x, p);
        let y1 = this.pntA.yTransform(this.pntA.y, p);
        let x2 = this.pntB.xTransform(this.pntB.x, p);
        let y2 = this.pntB.yTransform(this.pntB.y, p);

        let points = [
            [bx, by],
            [bx, yInit],
            [xInit, yInit],
            [xInit, by]
        ];

        points.forEach((pnt, i) => {
            let pntA = points[i];
            let pntB = points[(i + 1) % points.length];

            if(this.pntA.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x1, y1, -1000, -1000)) {
                intersections1 += 1;
            }
            if(this.pntA.vectorIntersection(pntA[0], pntA[1], pntB[0], pntB[1], x2, y2, -1000, -1000)) {
                intersections2 += 1;
            }
        });

        if(intersections1 % 2 !== 0 || intersections2 % 2 !== 0) {
            this.selected = true;
        }
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