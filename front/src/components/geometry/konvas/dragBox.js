import Constant from "./../constants";

class DragBox {

    constructor(polygons, width, height, stage, layer) {
        // draw a background rect to catch events.
        let rectArray = [
            {x: -10, y: -10},
            {x: -10, y: height + 10},
            {x: width + 10, y: height + 10},
            {x: width + 10, y: -10},
        ];
        this.r1 = new Konva.Shape({
            sceneFunc: (context, shape) => {
                context.beginPath();
                context.moveTo(
                    this.xTransform(rectArray[0].x),
                    this.yTransform(rectArray[0].y),
                );
                rectArray.forEach((pnt, index) => {
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
            },
            stroke: '#303030',
            strokeWidth: 1,
        });

        // draw a rectangle to be used as the rubber area
        this.r2 = new Konva.Rect({x: 0, y: 0, width: 0, height: 0, stroke: 'red', dash: [2,2]});
        this.r2.listening(false); // stop r2 catching our mouse events.

        let mode = '';
        this.posStart = null;
        this.posNow = null;
        this.stage = stage;
        this.layer = layer;
        this.height = height;
        this.width = width;

        this.polygons = polygons;


        this.r1.on('mousedown', (e) => {
            mode = 'drawing';
            this.startDrag({x: e.evt.layerX, y: e.evt.layerY});
        });

        this.r1.on('mousemove', (e) => {
            if (mode === 'drawing' || mode === 'moving') {
                mode = 'moving';
                this.updateDrag({x: e.evt.layerX, y: e.evt.layerY}, this.polygons, this.stage)
            }
        });

        this.r1.on('mouseup', (e) => {
            if (mode !== 'moving') {
                this.polygons.forEach(pol => {
                    pol.mouseClick([e.evt.layerX, e.evt.layerY]);
                });
            } else {
                this.polygons.forEach(pol => {
                    pol.dragBoxIntersect(this.r2);
                });
                this.r2.visible(false);
            }
            this.layer.draw();
            mode = '';

        });

        layer.add(this.r1);
        layer.add(this.r2);

    }

    startDrag(posIn){
        this.posStart = {x: posIn.x, y: posIn.y};
        this.posNow = {x: posIn.x, y: posIn.y};
    }

    reverse(r1, r2){
        let r1x = r1.x, r1y = r1.y, r2x = r2.x,  r2y = r2.y, d;
        if (r1x > r2x ){
            d = Math.abs(r1x - r2x);
            r1x = r2x; r2x = r1x + d;
        }
        if (r1y > r2y ){
            d = Math.abs(r1y - r2y);
            r1y = r2y; r2y = r1y + d;
        }
        return ({x1: r1x, y1: r1y, x2: r2x, y2: r2y}); // return the corrected rect.
    }

    updateDrag(posIn, polygons, stage){

        // update rubber rect position
        this.posNow = {x: posIn.x, y: posIn.y};
        let posRect = this.reverse(this.posStart, this.posNow);
        this.r2.x(posRect.x1);
        this.r2.y(posRect.y1);
        this.r2.width(posRect.x2 - posRect.x1);
        this.r2.height(posRect.y2 - posRect.y1);
        this.r2.visible(true);

        stage.draw();
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

}

export default DragBox;