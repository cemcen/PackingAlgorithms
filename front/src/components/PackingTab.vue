<template>
    <div class="fill-height">
        <v-toolbar dark>
            <v-toolbar-title>Packing</v-toolbar-title>
            <v-divider class="mx-2" inset vertical></v-divider>
            <v-spacer></v-spacer>

            <v-btn color="teal darken-1" dark class="mb-2">Execute Algorithm</v-btn>

        </v-toolbar>
        <div ref="polygonContainer" class="polygon">
            <div ref="polygonDrawer"></div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Packing",
        data() {
          return {
              ps: null,
          }
        },
        mounted() {
            this.script = p => {
                let canvas = null;
                // Settings of the canvas.
                p.setup = () => {
                    // We use the div size as the canvas size.
                    canvas = p.createCanvas(this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas.parent(this.$refs.polygonDrawer);
                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(60);
                    //console.log(canvas);
                };
                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(255);
                    p.push();
                    p.translate(p.width*0.5, p.height*0.5);
                    p.rotate(p.frameCount / 200.0);
                    this.drawPolygon(0, 0, this.radius, this.numberOfVertex, p);
                    p.pop();
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script);
        },
        methods: {
            drawPolygon(x, y, radius, npoints, p) {
                const angle = p.TWO_PI / npoints;
                p.beginShape();
                for (let a = 0; a < p.TWO_PI; a += angle) {
                    const sx = x + p.cos(a) * radius;
                    const sy = y + p.sin(a) * radius;
                    p.vertex(sx, sy);
                }
                p.endShape(p.CLOSE);
            },
        }
    }
</script>

<style scoped>
    .polygon {
        height: 100%;
        min-width: 100%;
    }
</style>