<template>
    <v-dialog eager v-model="dialog" fullscreen persistent max-width="500px" style="max-height: 100%">
        <v-toolbar dark color="primary">
            <v-btn icon dark @click="closeDialog()">
                <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Results</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip left>
                <template v-slot:activator="{ on }">
                    <v-btn icon color="white" v-on="on" @click="downloadImage">
                        <v-icon>mdi-file-image</v-icon>
                    </v-btn>
                </template>
                <span>Download Image</span>
            </v-tooltip>
        </v-toolbar>
        <v-card class="fill-height">
            <v-row class="fill-height" no-gutters>
                <v-col md="6" class="fill-height">
                    <div id='myContainerUR' ref="pCont" class="polygon">
                        <div id="polygonDrawerUpdateResults" ref="polygonDrawer"></div>
                    </div>
                </v-col>
                <v-col md="6" class="fill-height">
                    <upload-results-file @reDraw="refresh"/>
                </v-col>
            </v-row>
        </v-card>
    </v-dialog>
</template>

<script>

    import Constant from "../geometry/constants";
    import UploadResultsFile from "./UploadResultsFile.vue";

    export default {
        name: "UploadResultsDialog",
        components: {UploadResultsFile},
        data() {
            return {
                dialog: false,
                ps: null,
            }
        },
        mounted() {
            this.script = p => {
                let canvas = null;
                // Settings of the canvas.
                p.setup = () => {
                    // We use the div size as the canvas size.
                    //console.log(this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas = p.createCanvas(this.$refs.pCont.clientWidth, this.$refs.pCont.clientHeight);//this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas.parent(this.$refs.polygonDrawer);

                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(1);
                    //console.log(canvas);
                };
                p.windowResized = () => {
                    if (typeof this.$refs.pCont !== "undefined") {
                        p.resizeCanvas(this.$refs.pCont.clientWidth, this.$refs.pCont.clientHeight);
                    }
                };


                // What's been drawn on the canvas
                p.draw = () => {
                    if (this.dialog) {
                        p.background(255, 255, 255);
                        p.noFill();
                        p.push();
                        this.drawGraph(p);
                        this.colorPolygons(p);
                        p.pop();
                    }
                };


            };

            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainerUR');
        },
        computed: {
            packing() {
                return this.$store.getters.getPacking;
            }
        },
        methods: {
            openDialog() {
                this.dialog = true;
            },
            closeDialog() {
                this.dialog = false;
            },
            downloadImage() {
                let filename = 'results.png';
                this.ps.save(filename);
            },
            reDraw() {
                let ps = this.ps;
                setTimeout(function () {
                    ps.windowResized();
                }, 310);
            },
            getWidth(p) {
                return p.width - Constant.WIDTH_OFFSET;
            },
            getHeight(p) {
                return p.height - Constant.HEIGHT_OFFSET;
            },
            getOffsetXAxis() {
                return Constant.X_OFFSET;
            },
            getOffsetYAxis() {
                return Constant.Y_OFFSET;
            },
            drawGraph(p) {
                let graph = this.packing.graph;
                let height = this.packing.height;
                let width = this.packing.width;
                let widthContainer = this.getWidth(p);
                let heightContainer = this.getHeight(p);
                let xAxisOffset = this.getOffsetXAxis();
                let yAxisOffset = this.getOffsetYAxis();

                if (graph) {
                    Object.keys(graph).forEach(function (pointA) {
                        Object.keys(graph[pointA]).forEach(function (pointB) {
                            const pntA = JSON.parse("[" + pointA + "]");
                            const pntB = JSON.parse("[" + pointB + "]");
                            p.stroke(33, 33, 33);
                            p.strokeWeight(2);
                            p.line(
                                ((pntA[0] / width) * widthContainer) + xAxisOffset,
                                (((height - pntA[1]) / height) * heightContainer) + yAxisOffset,
                                ((pntB[0] / width) * widthContainer) + xAxisOffset,
                                (((height - pntB[1]) / height) * heightContainer) + yAxisOffset
                            );
                        });
                    });
                }
            },
            colorPolygons(p) {
                let width =  this.packing.width;
                let height =  this.packing.height;
                this.packing.polygons.forEach(pol => {
                    let polygon = pol;
                    p.stroke(33, 33, 33);
                    p.strokeWeight(1);
                    p.colorMode(p.HSB);
                    if (polygon.type) {
                        if(polygon.color && polygon.type === 'Stresses') {
                            let color = polygon.color;
                            if (isNaN(color[0])) color[0] = 0;
                            p.fill(color[0], 100, 100);
                            p.stroke(color[0], 100, 100);
                        }
                        p.beginShape();
                        polygon.points.forEach(pnt => {
                            if(pnt.color && polygon.type === 'Displacements') {
                                let color = pnt.color;
                                if (isNaN(color)) color = 0;
                                p.fill(color, 100, 100);
                                p.stroke(color, 100, 100);
                            }
                            let sx = ((pnt.x / width) * this.getWidth(p)) + this.getOffsetXAxis();
                            let sy = (((height - pnt.y) / height) * this.getHeight(p)) + +this.getOffsetYAxis();

                            p.vertex(sx, sy);
                        });
                        p.endShape(p.CLOSE);
                    }

                });
            },
            refresh(){
                this.ps.draw();
            }
        }
    }
</script>

<style scoped>
    .polygon {
        padding: 0;
        margin: 0;
        height: 80%;
        min-width: 100%;
    }

    .hint-style {
        font-size: smaller;
        color: #666;
    }
</style>