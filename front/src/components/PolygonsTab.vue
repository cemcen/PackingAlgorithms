<template>
    <div class="page-container">
        <v-content class="md-accent">Elegir nuevo polígono</v-content>
        <div ref="polygonContainer" class="polygon">
            <div ref="polygonDrawer"></div>
        </div>
        <v-form v-model="valid">
            <v-text-field v-model="polygon.label" label="Label" required></v-text-field>
            <v-text-field v-model="polygon.numberOfVertex" type="number" label="Number of vertex" required></v-text-field>
            <v-text-field v-model="polygon.radius" type="number" label="Radius" required></v-text-field>
            <v-text-field v-model="polygon.percentage" type="number" label="Number of appearances" required></v-text-field>
        </v-form>
        <v-btn color="primary" @click.native="savePolygon">Add polygon</v-btn>
    </div>
</template>

<script>
    export default {
        data(){
            return {
                selectedTab: 0,
                script: null,
                numberOfVertex: 4,
                radius: 40,
                polygons: [],
                ps: null,
                polygon: {
                    label: "",
                    numberOfVertex: 4,
                    radius: 40,
                    percentage: null
                }
            }
        },
        name: "PolygonsTab",
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
            if(localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
        },
        watch: {
            polygons: {
                handler() {
                    localStorage.setItem('polygons', JSON.stringify(this.polygons));
                },
                deep: true
            },
        },
        methods: {
            drawPolygon(x,y, radius, npoints, p) {
                const angle = p.TWO_PI / npoints;
                p.beginShape();
                for (let a = 0; a < p.TWO_PI; a += angle) {
                    const sx = x + p.cos(a) * radius;
                    const sy = y + p.sin(a) * radius;
                    p.vertex(sx, sy);
                }
                p.endShape(p.CLOSE);
            },
            changedNumberOfVertex(){
                this.numberOfVertex = this.polygon.numberOfVertex;
            },
            savePolygon(){
                this.polygons.push({
                    "label": this.polygon.label,
                    "numberOfVertex": parseInt(this.polygon.numberOfVertex),
                    "percentage": parseInt(this.polygon.percentage),
                    "radius": parseInt(this.polygon.radius)
                });
            }
        }
    }
</script>

<style scoped lang="scss">
    .polygon {
        min-height: 10em;
        min-width: 100%;
        border: 1px solid rgba(#000, .12);
    }
    .md-content {
        min-width: 100%;
        height: 3em;
        display: inline-flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 1px;
        margin-top: 1px;
    }
    .page-container{
        max-height: 30em;
        overflow:auto;
        margin: 2px;
    }
</style>