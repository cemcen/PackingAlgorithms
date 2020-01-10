<template>
    <v-dialog eager v-model="dialog" fullscreen persistent max-width="500px">
        <v-toolbar dark color="primary">
            <v-btn icon dark @click="closeDialog()">
                <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Assign Border Conditions</v-toolbar-title>
            <v-spacer></v-spacer>
        </v-toolbar>
        <v-card class="ninety_percent_height">
            <v-row class="fill-height">
                <v-col md="6" class="fill-height">
                    <div id='myContainerBC' ref="pCont" class="polygon">
                        <div id="polygonDrawerBorderConditions" ref="polygonDrawer"></div>
                    </div>
                </v-col>
                <v-col md="6">

                </v-col>
            </v-row>
            <v-card-actions>
                <v-spacer></v-spacer>

            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>

    import Constant from "./geometry/constants";
    import Point from "./geometry/point";
    import Segment from "./geometry/segment";

    export default {
        name: "BorderConditions",
        props: {
            dialog: {
                type: Boolean,
                defaultValue: false,
            },
        },
        data() {
            return {
                ps: null,
                borderPoints: [],
                borderSegments: [],
                packing: {
                    height: 0,
                    width: 0,
                    polygons: [],
                    graph: {},
                    draw: {
                        points: {},
                        edges: {},
                        polygons: {},
                    }
                },
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
                    this.loadBorderElements();

                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(5);
                    //console.log(canvas);
                };
                p.windowResized = () => {
                    if (typeof this.$refs.pCont !== "undefined") {
                        p.resizeCanvas(this.$refs.pCont.clientWidth, this.$refs.pCont.clientHeight);
                    }
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    if(this.dialog) {
                        p.background(255, 255, 255);
                        p.noFill();
                        p.push();
                        this.drawGraph(p);
                        this.drawBorderElements(p);
                        p.pop();
                    }
                };

                p.mousePressed = () => {
                    if(this.dialog) {
                        this.borderSegments.forEach(seg => {
                            seg.mousePressed(p);
                        });
                        this.borderPoints.forEach(pnt => {
                            pnt.mousePressed(p);
                        });
                    }
                };

            };

            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainerBC');
            if (localStorage.getItem('packing')) {
                this.packing = JSON.parse(localStorage.getItem('packing'));
                this.packing.polygons.map(pol => pol.selected = false);
                this.borderPoints = [];
                this.borderSegments = [];
            }
        },
        methods: {
            closeDialog() {
                this.$emit('closeDialog', false);
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
            updatePacking(packing) {
                this.packing = packing;
                this.packing.polygons.map(pol => pol.selected = false);
                this.borderPoints = [];
                this.borderSegments = [];
                this.loadBorderElements();
                this.$forceUpdate();
            },
            loadBorderElements() {
                let borderPointsArray = this.packing.draw.borderPoints;
                let bPDict = {};
                Object.keys(borderPointsArray).forEach(bp => {
                    const pntA = JSON.parse("[" + bp + "]");
                    bPDict[borderPointsArray[bp].pointIndex] = pntA;
                    this.borderPoints.push(new Point(pntA[0], pntA[1], this.packing.width, this.packing.height))
                });
                let borderSegmentsArray = this.packing.draw.borderSegments;
                Object.keys(borderSegmentsArray).forEach(bs => {
                    let split = bs.split(",");
                    let pntA = bPDict[split[0]];
                    let pntB = bPDict[split[1]];
                    this.borderSegments.push(new Segment(pntA[0], pntA[1], pntB[0], pntB[1] ,this.packing.width, this.packing.height));
                });
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
                            p.stroke(111, 111, 111);
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
            drawBorderElements(p) {
                this.borderSegments.forEach(seg => {
                    seg.checkMouseOver(p);
                    seg.draw(p);
                });
                this.borderPoints.forEach(pnt => {
                    pnt.checkMouseOver(p);
                    pnt.draw(p);
                });
            }
        }
    }
</script>

<style scoped>
    .min-size-card {
        height: 250px;
        width: 500px;
    }

    .ninety_percent_height{
        height: 50% !important;
    }

    .polygon {
        padding: 0;
        margin: 0;
        height: 80%;
        min-width: 100%;
    }
</style>