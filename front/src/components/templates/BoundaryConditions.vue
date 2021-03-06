<template>
    <v-dialog eager v-model="dialog" fullscreen persistent max-width="500px" style="max-height: 100%">
        <v-toolbar dark color="primary">
            <v-btn icon dark @click="closeDialog()">
                <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Boundary Conditions</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip left>
                <template v-slot:activator="{ on }">
                    <v-btn color="white" v-on="on" icon @click.native="loadOriginal">
                        <v-icon>mdi-backup-restore</v-icon>
                    </v-btn>
                </template>
                <span>Load Original Packing</span>
            </v-tooltip>
            <v-tooltip left>
                <template v-slot:activator="{ on }">
                    <v-btn icon color="white" v-on="on" @click="addMoreBorderPoints">
                        <v-icon>mdi-shape-square-plus</v-icon>
                    </v-btn>
                </template>
                <span>Add more border nodes</span>
            </v-tooltip>
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
                    <v-flex class="d-flex justify-center">
                        <span class="hint-style">Multiples segments and nodes on the boundary can be selected by holding down the OPTION/ALT button.</span>
                    </v-flex>

                    <div id='myContainerBC' ref="pCont" class="polygon">
                        <div id="polygonDrawerBorderConditions" ref="polygonDrawer"></div>
                    </div>

                </v-col>
                <v-col md="6" class="fill-height">
                    <boundary-properties @assign-properties="assignProperties"/>
                </v-col>
            </v-row>
        </v-card>
        <add-border-points ref="addBorderPointRef" @changedBoundary="changedBoundary" :bs="borderSegments"/>
    </v-dialog>
</template>

<script>

    import Constant from "../geometry/constants";
    import Point from "../geometry/point";
    import Segment from "../geometry/segment";
    import BoundaryProperties from "./BoundaryProperties.vue";
    import AddBorderPoints from "./AddBorderPointsDialog.vue";

    export default {
        name: "BorderConditions",
        components: {AddBorderPoints, BoundaryProperties},
        props: {
            dialog: {
                type: Boolean,
                defaultValue: false,
            },
        },
        data() {
            return {
                ps: null,
                drawPacking: false,
                drawSelectionBox: false,
                borderPoints: [],
                borderSegments: [],
            }
        },
        computed: {
            properties() {
                return this.$store.getters.getProperties;
            },
            packing() {
                return this.$store.getters.getPacking;
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
                    canvas.mousePressed(() => {
                        if (this.dialog) {
                            this.borderSegments.forEach(seg => {
                                seg.mousePressed(p);
                            });
                            this.borderPoints.forEach(pnt => {
                                pnt.mousePressed(p);
                            });
                        }
                    });

                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(32);
                    //console.log(canvas);
                };
                p.windowResized = () => {
                    if (typeof this.$refs.pCont !== "undefined") {
                        p.resizeCanvas(this.$refs.pCont.clientWidth, this.$refs.pCont.clientHeight);
                        this.drawPacking = true;
                        p.draw();
                    }
                };

                let locked = false;
                let dragged = false;
                let xInit = 0;
                let yInit = 0;
                let bx = 0;
                let by = 0;
                p.mousePressed = () => {
                    if (p.mouseX > -10 && p.mouseY > -10 && p.mouseX < p.width + 10 && p.mouseY < p.height + 10 && this.dialog) {
                        locked = true;
                        xInit = p.mouseX;
                        yInit = p.mouseY;
                        bx = p.mouseX;
                        by = p.mouseY;
                    }
                };

                p.mouseDragged = () => {
                    if (locked) {
                        dragged = true;
                        bx = p.mouseX;
                        by = p.mouseY;
                        this.drawSelectionBox = true;
                        p.draw();
                    }
                };

                p.mouseReleased = () => {
                    locked = false;
                    if (dragged && this.dialog) {
                        this.borderSegments.forEach(seg => {
                            seg.checkIntersectionWithBox(p, bx, by, xInit, yInit);
                        });
                        this.borderPoints.forEach(pnt => {
                            pnt.isInsideBox(p, bx, by, xInit, yInit);
                        });
                        this.drawPacking = true;
                        p.draw();
                    }

                    xInit = 0;
                    yInit = 0;
                    bx = 0;
                    by = 0;
                };


                // What's been drawn on the canvas
                p.draw = () => {
                    if (this.dialog && (this.drawPacking || this.drawSelectionBox)) {
                        p.background(255, 255, 255);
                        p.noFill();
                        p.push();
                        this.drawPacking = false;
                        this.drawGraph(p);
                        this.drawBorderElements(p);

                        if (locked && this.drawSelectionBox) {
                            this.drawSelectionBox = false;
                            p.strokeWeight(3);
                            p.stroke(239, 83, 80);
                            p.noFill();
                            let x = Math.min(bx, xInit);
                            let y = Math.min(by, yInit);
                            p.rect(x, y, Math.abs(bx - xInit), Math.abs(by - yInit))
                        }
                        p.pop();
                    }
                };


            };

            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainerBC');
        },
        methods: {
            closeDialog() {
                this.$emit('closeDialog', false);
                this.resetSelectedProperties();
            },
            resetSelectedProperties() {
                let nProperties = this.properties;
                Object.keys(nProperties).forEach(function (item) {
                    nProperties[item].selected = false;
                });
                this.$store.commit("editProperties", nProperties);
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
            updatePacking() {
                this.loadBorderElements();
            },
            loadBorderElements() {
                this.borderPoints = [];
                this.borderSegments = [];
                if (this.packing && this.packing.draw) {
                    let borderPointsArray = this.packing.draw.borderPoints;
                    let bPDict = {};
                    Object.keys(borderPointsArray).forEach(bp => {
                        const pntA = JSON.parse("[" + bp + "]");
                        bPDict[borderPointsArray[bp].pointIndex] = pntA;
                        this.borderPoints.push(new Point(pntA[0], pntA[1], this.packing.width, this.packing.height, bp, borderPointsArray[bp].properties))
                    });
                    let borderSegmentsArray = this.packing.draw.borderSegments;
                    Object.keys(borderSegmentsArray).forEach(bs => {
                        let split = bs.split(",");
                        let pntA = bPDict[split[0]];
                        let pntB = bPDict[split[1]];
                        this.borderSegments.push(new Segment(pntA[0], pntA[1], pntB[0], pntB[1],
                            this.packing.width, this.packing.height, bs, borderSegmentsArray[bs].properties,
                            borderSegmentsArray[bs].indexPol));
                    });
                    this.drawPacking = true;
                    this.ps.draw();
                }
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
                    seg.checkMouseOver(p, this.properties);
                    seg.draw(p);
                });
                this.borderPoints.forEach(pnt => {
                    pnt.checkMouseOver(p, this.properties);
                    pnt.draw(p);
                });
            },
            assignProperties(selectedOptionProperties, selectedOptionType) {
                let sOP = selectedOptionProperties === "All" ? 0 : 1;
                let sOT = selectedOptionType === "All" ? 0 : (selectedOptionType === "All Nodes" ? 1 : 2);
                let borderPointsArray = this.packing.draw.borderPoints;
                let borderSegmentsArray = this.packing.draw.borderSegments;
                let properties = this.properties;
                this.borderPoints.forEach(pnt => {
                    if (sOP === 0 || (pnt.isSelected() && sOP === 1)) {
                        if (sOT === 0 || sOT === 1) {
                            borderPointsArray[pnt.getKey()].properties = [];
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    borderPointsArray[pnt.getKey()].properties.push({
                                        key: item,
                                        value: properties[item].default
                                    })
                                }
                            });
                        }
                    }
                });
                this.borderSegments.forEach(seg => {
                    if (sOP === 0 || (seg.isSelected() && sOP === 1)) {
                        if (sOT === 0 || sOT === 2) {
                            borderSegmentsArray[seg.getKey()].properties = [];
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    borderSegmentsArray[seg.getKey()].properties.push({
                                        key: item,
                                        value: properties[item].default
                                    })
                                }
                            });
                        }
                    }
                });
                this.$store.commit("updateBorderConditions", {
                    borderPointsArray: borderPointsArray,
                    borderSegmentsArray: borderSegmentsArray,
                });
                this.loadBorderElements();
            },
            downloadImage() {
                let filename = 'border_conditions.png';
                this.ps.save(filename);
            },
            addMoreBorderPoints() {
                this.$refs.addBorderPointRef.openDialog();
            },
            changedBoundary() {
                this.$emit("changedBoundary");
            },
            loadOriginal() {
                this.$emit("loadOriginal");
            }
        }
    }
</script>

<style scoped>
    .min-size-card {
        height: 250px;
        width: 500px;
    }

    .ninety_percent_height {
        height: 50% !important;
    }

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