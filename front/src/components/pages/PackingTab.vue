<template>
    <div class="page-container">
        <v-layout class="all-height">
            <v-flex sm9 pa-2>
                <v-card class="my-card">
                    <v-toolbar color="#eeeeee">
                        <v-toolbar-title>Options</v-toolbar-title>
                        <v-divider class="mx-2" inset vertical></v-divider>

                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn color="teal lighten-2" text :disabled="executing" @click="dialog = true" icon
                                       v-on="on">
                                    <v-icon>mdi-plus-circle</v-icon>
                                </v-btn>
                            </template>
                            <span>Create New Packing</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on" @click="openAssignProp">
                                    <v-icon>mdi-palette</v-icon>
                                </v-btn>
                            </template>
                            <span>Assign Properties</span>
                        </v-tooltip>

                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2":disabled="executing" v-on="on" @click="exportPacking">
                                    <v-icon>mdi-download</v-icon>
                                </v-btn>
                            </template>
                            <span>Download Mesh</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2":disabled="executing" v-on="on" @click="importPacking">
                                    <v-icon>mdi-upload</v-icon>
                                </v-btn>
                            </template>
                            <span>Upload Mesh</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2":disabled="executing" v-on="on" @click="downloadImage">
                                    <v-icon>mdi-file-image</v-icon>
                                </v-btn>
                            </template>
                            <span>Download Image</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2":disabled="executing" v-on="on" @click="openAngleDialog()">
                                    <v-icon>mdi-angle-acute</v-icon>
                                </v-btn>
                            </template>
                            <span>Minimum Angle</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" v-on="on" :disabled="executing"
                                       @click="openBorderConditions()">
                                    <v-icon>mdi-selection-drag</v-icon>
                                </v-btn>
                            </template>
                            <span>Boundary Conditions</span>
                        </v-tooltip>

                        <v-spacer></v-spacer>

                        <v-progress-circular v-show="executing" indeterminate
                                             color="teal lighten-2"></v-progress-circular>
                        <v-dialog v-model="dialog" persistent max-width="700px">
                            <v-card color="#ffffff">
                                <v-card-title>
                                    <span class="headline">New Packing</span>
                                </v-card-title>

                                <v-tabs v-model="tab" slider-color="teal lighten-2" color="teal lighten-2"
                                        background-color="transparent" grow>
                                    <v-tab v-for="item in piledOption" :key="item">
                                        {{ item }}
                                    </v-tab>
                                </v-tabs>
                                <v-card-text>
                                    <div v-show="tab === 0">
                                        <v-container>
                                            <v-row justify="center">
                                                <v-flex pr-3>
                                                    <v-text-field v-validate="'required|min_value:1'"
                                                                  :error-messages="errors.collect('width')"
                                                                  v-model="width" type="number" label="Container Width"
                                                                  data-vv-name="width" clearable required>
                                                    </v-text-field>
                                                </v-flex>
                                                <v-flex>
                                                    <v-text-field v-validate="'required|min_value:1'"
                                                                  :error-messages="errors.collect('height')"
                                                                  v-model="height" type="number"
                                                                  label="Container Height"
                                                                  data-vv-name="height" clearable required>
                                                    </v-text-field>
                                                </v-flex>
                                            </v-row>
                                            <v-row justify="center">
                                                <v-flex>
                                                    <v-text-field v-validate="'required|min_value:1|max_value:100'"
                                                                  :error-messages="errors.collect('regularity')"
                                                                  v-model="regularity" type="number"
                                                                  label="Polygons Regularity"
                                                                  hint="Variability of an edge (5% of variability)"
                                                                  persistent-hint
                                                                  data-vv-name="regularity" clearable>
                                                    </v-text-field>
                                                </v-flex>
                                            </v-row>
                                            <v-row justify="center">
                                                <v-flex>
                                                    <v-radio-group v-validate="'required'" label="Choose approach"
                                                                   :error-messages="errors.collect('approach')"
                                                                   v-model="approach"
                                                                   data-vv-name="regularity" row>
                                                        <v-radio color="teal lighten-2" label="Dense packing"
                                                                 :value="1"></v-radio>
                                                        <v-radio color="teal lighten-2" label="Gravity simulation"
                                                                 :value="0"></v-radio>
                                                    </v-radio-group>
                                                </v-flex>
                                            </v-row>
                                        </v-container>
                                    </div>
                                    <div v-show="tab === 1">
                                        <v-container>
                                            <v-row justify="center">
                                                <v-btn color="teal lighten-2" class="mr-2" dark @click="addLayer">
                                                    Add Layer
                                                </v-btn>
                                                <v-btn :disabled="layers.length === 1" :dark="layers.length !== 1"
                                                       color="teal lighten-2"
                                                       @click="deleteLayer">
                                                    Remove Layer
                                                </v-btn>
                                            </v-row>
                                            <v-row justify="center">
                                                <v-flex>
                                                    <v-text-field v-validate="'required|min_value:1'"
                                                                  :error-messages="errors.collect('width')"
                                                                  v-model="width" type="number" label="Container Width"
                                                                  data-vv-name="width" clearable required>
                                                    </v-text-field>
                                                </v-flex>
                                            </v-row>
                                            <v-layout v-for="(item, index) in layers" :key="index" column>
                                                <v-row>
                                                    <v-flex pr-3>
                                                        <v-text-field v-validate="'required|min_value:1'"
                                                                      :error-messages="errors.collect(`height${index}`)"
                                                                      v-model="layers[index].height" type="number"
                                                                      label="Container Height"
                                                                      :data-vv-name="`height${index}`" clearable
                                                                      required>
                                                        </v-text-field>
                                                    </v-flex>
                                                    <v-flex>
                                                        <v-text-field v-validate="'required|min_value:1|max_value:100'"
                                                                      :error-messages="errors.collect(`regularity${index}`)"
                                                                      v-model="layers[index].regularity" type="number"
                                                                      label="Polygons Regularity"
                                                                      hint="Variability of an edge (5% of variability)"
                                                                      persistent-hint
                                                                      :data-vv-name="`regularity${index}`" clearable>
                                                        </v-text-field>
                                                    </v-flex>
                                                </v-row>
                                                <v-row>
                                                    <v-flex>
                                                        <v-select
                                                                v-model="layers[index].polygons" :items="polygons"
                                                                item-text="label" item-color="teal lighten-2"
                                                                :data-vv-name="`polygons${index}`"
                                                                color="teal lighten-2" chips return-object
                                                                label="Layer Polygons" multiple>
                                                        </v-select>
                                                    </v-flex>
                                                </v-row>
                                            </v-layout>
                                            <v-row justify="center">
                                                <v-flex>
                                                    <v-radio-group v-validate="'required'" label="Choose approach"
                                                                   :error-messages="errors.collect('approach')"
                                                                   v-model="approach"
                                                                   data-vv-name="regularity" row>
                                                        <v-radio color="teal lighten-2" label="Dense packing"
                                                                 :value="1"></v-radio>
                                                        <v-radio color="teal lighten-2" label="Gravity simulation"
                                                                 :value="0"></v-radio>
                                                    </v-radio-group>
                                                </v-flex>
                                            </v-row>
                                        </v-container>
                                    </div>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" text @click.native="dialog = false">Close</v-btn>
                                    <v-btn v-if="tab === 0" dark color="teal lighten-2" @click.native="execute">Execute
                                        Algorithm
                                    </v-btn>
                                    <v-btn v-else dark color="teal lighten-2" @click.native="executeMultiLayer">Execute
                                        Algorithm
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <assign-properties :dialog="dialog2"
                                           @closeDialog="dialog2 = false"
                                           :properties="properties"
                                           @assignProperties="assignProperties"/>

                        <v-dialog v-model="dialogAngle" persistent max-width="500px">
                            <v-card>
                                <v-card-title>
                                    <span class="headline">Minimum Layout Angle</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-layout justify-center>
                                        <v-flex>
                                            <v-text-field v-validate="'required'"
                                                          :error-messages="errors.collect('minimumAngle')"
                                                          v-model="minimumAngle"
                                                          label="Minimum Angle"
                                                          type="number"
                                                          data-vv-name="minimumAngle"
                                                          clearable
                                                          required>
                                            </v-text-field>
                                        </v-flex>
                                    </v-layout>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" text @click.native="dialogAngle = false">Close
                                    </v-btn>
                                    <v-btn color="teal lighten-2" text @click.native="loadOriginal">Load Original
                                    </v-btn>
                                    <v-btn dark color="teal lighten-2" @keyup.enter="optimizeAngle"
                                           @click.native="optimizeAngle">Optimize
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <boundary-conditions ref="boundaryConditionsComponent" :dialog="dialogBoundaryConditions" :properties="properties"
                                           @closeDialog="dialogBoundaryConditions = false"/>
                        <import-packing ref="refImportPacking" @loadtxtpacking="loadTxtPacking"/>
                        <download-packing ref="refExportPacking"/>

                    </v-toolbar>
                    <div id='myContainer' ref="polygonContainer" class="polygon">
                        <div ref="polygonDrawer"></div>
                    </div>
                </v-card>
            </v-flex>
            <v-flex sm3 pa-2>
                <v-card class="my-card">
                    <v-tabs grow color="teal lighten-2">
                        <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

                        <!--<v-tab id="tab-info" @click="selectTab(2)">
                            <v-icon>info</v-icon> Info
                        </v-tab>-->

                        <v-tab id="tab-polygon" @click="selectTab(1)">
                            <v-icon>mdi-shape</v-icon>
                            Polygons
                        </v-tab>

                        <v-tab id="tab-distribution" @click="selectTab(0)">
                            <v-icon>mdi-clipboard-text</v-icon>
                            Properties
                        </v-tab>
                    </v-tabs>
                    <div v-show="selectedTab === 0" class="max-height-card">
                        <properties-tab ref="propertiesTab"></properties-tab>
                    </div>

                    <div v-show="selectedTab === 1" class="max-height-card">
                        <polygons-tab></polygons-tab>
                    </div>

                    <div v-show="selectedTab === 2" class="max-height-card">
                        <info-tab :selected-polygons="getSelectedPolygons"></info-tab>
                    </div>
                </v-card>
            </v-flex>
        </v-layout>

        <v-snackbar color="teal lighten-2" v-model="snackbar" bottom :timeout="timeout">
            {{ snackbarMessage }}
        </v-snackbar>
    </div>
</template>

<script>
    import PropertiesTab from "./PropertiesTab.vue";
    import PolygonsTab from "./PolygonsTab.vue";
    import InfoTab from "../InfoTab.vue";
    import api from "../../services/api.services";
    import * as poly2tri from 'poly2tri';
    import Swatches from 'vue-swatches';

    // Import the styles too, globally
    import "vue-swatches/dist/vue-swatches.min.css"
    import BoundaryConditions from "../templates/BoundaryConditions.vue";
    import AssignProperties from "../templates/AssignProperties.vue";
    import ImportPacking from "../templates/ImportPacking.vue";
    import DownloadPacking from "../templates/DownloadPacking.vue";

    const routes = ["/properties", "/polygons", "/info"];

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        components: {
            DownloadPacking,
            ImportPacking,
            AssignProperties,
            BoundaryConditions,
            InfoTab,
            PolygonsTab,
            PropertiesTab,
            Swatches,
        },
        name: "packing",
        data() {
            return {
                selectedTab: 1,
                ps: null,
                dialogBoundaryConditions: false,
                snackbar: false,
                snackbarMessage: '',
                timeout: 1200,
                layers: [{
                    height: 75,
                    regularity: 5
                }],
                width: 150,
                height: 75,
                randomShape: false,
                piledOption: ["Single Layer", "Multiple Layers"],
                tab: 0,
                regularity: 5,
                approach: 1,
                dialog: false,
                dialog2: false,
                dialogAngle: false,
                minimumAngle: 30,
                show: false,
                dialogInfo: false,
                executing: false,
                polygon: {
                    points: []
                },
            }
        },
        created() {
            this.selectTab(this.selectedTab);
        },
        computed: {
            getSelectedPolygons() {
                return (this.packing && this.packing.polygons)? this.packing.polygons.filter(pol => pol.selected): [];
            },
            properties () {
                return this.$store.getters.getProperties;
            },
            polygons () {
                return this.$store.getters.getPolygons;
            },
            packing () {
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
                    canvas = p.createCanvas(this.$refs.polygonContainer.clientWidth, this.$refs.polygonContainer.clientHeight);//this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas.parent(this.$refs.polygonDrawer);
                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(1);
                    //console.log(canvas);
                };

                p.windowResized = () => {
                    if (typeof this.$refs.polygonContainer !== "undefined") {
                        p.resizeCanvas(this.$refs.polygonContainer.clientWidth, this.$refs.polygonContainer.clientHeight)
                    }
                };

                let locked = false;
                let dragged = false;
                let xInit = 0;
                let yInit = 0;
                let bx = 0;
                let by = 0;
                p.mousePressed = () => {
                    if (p.mouseX > -10 && p.mouseY > -10 && p.mouseX < p.width + 10 && p.mouseY < p.height + 10 && !this.dialog2) {
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
                        p.draw();
                        bx = p.mouseX;
                        by = p.mouseY;
                    }
                };

                p.mouseReleased = () => {
                    locked = false;
                    if (dragged) {
                        let box = {
                            points: [
                                {
                                    x: Math.min(bx, xInit),
                                    y: Math.min(by, yInit)
                                },
                                {
                                    x: Math.max(bx, xInit),
                                    y: Math.min(by, yInit)
                                },
                                {
                                    x: Math.max(bx, xInit),
                                    y: Math.max(by, yInit)
                                },
                                {
                                    x: Math.min(bx, xInit),
                                    y: Math.max(by, yInit)
                                },
                            ]
                        };
                        let height = this.packing.height;
                        let width = this.packing.width;
                        if(this.packing.graph) {
                            this.packing.polygons.forEach(pol => {
                                pol.selected = false;
                            });
                            this.packing.polygons.forEach(pol => {
                                pol.selected = this.polygonIntersection(pol, box, width, height, p);
                            });
                        }
                        dragged = false;
                        p.draw();
                    }

                    xInit = 0;
                    yInit = 0;
                    bx = 0;
                    by = 0;
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(255, 255, 255);
                    p.noFill();
                    p.push();
                    if(this.packing) {
                        if (this.packing.polygons) {
                            this.packing.polygons.forEach(pol => {
                                this.drawPolygon(pol, this.packing.width, this.packing.height, p)
                            });
                        }
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
                                    p.strokeWeight(3);

                                    p.line(
                                        ((pntA[0] / width) * widthContainer) + xAxisOffset,
                                        (((height - pntA[1]) / height) * heightContainer) + yAxisOffset,
                                        ((pntB[0] / width) * widthContainer) + xAxisOffset,
                                        (((height - pntB[1]) / height) * heightContainer) + yAxisOffset
                                    );
                                });
                            });
                        }
                    }
                    if (locked) {
                        p.strokeWeight(3);
                        p.stroke(239, 83, 80);
                        p.noFill();
                        let x = Math.min(bx, xInit);
                        let y = Math.min(by, yInit);
                        p.rect(x, y, Math.abs(bx - xInit), Math.abs(by - yInit))
                    }
                    p.pop();
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainer');
        },
        methods: {
            getWidth(p) {
                return p.width - 30;
            },
            getHeight(p) {
                return p.height - 30;
            },
            getOffsetXAxis() {
                return 10;
            },
            getOffsetYAxis() {
                return 10;
            },
            selectTab(i) {
                this.selectedTab = i;
                this.$router.push(routes[i]);
            },
            openAngleDialog() {
                this.dialogAngle = true;
            },
            openBorderConditions() {
                this.dialogBoundaryConditions = true;
                this.$refs.boundaryConditionsComponent.reDraw();
            },
            optimizeAngle() {
                let minimumRadianAngle = this.minimumAngle * Math.PI / 180;
                let changed = false;
                this.packing.polygons.map(pol => {
                    if (pol.hole) {
                        let deletedPoints = [];
                        for (let i = 0; i < pol.points.length; i += 1) {
                            let p1 = pol.points[i];
                            let p2 = pol.points[(i + 1) % pol.points.length];
                            let p3 = pol.points[(i + 2) % pol.points.length];

                            let p21 = [p1.x - p2.x, p1.y - p2.y];
                            let p23 = [p3.x - p2.x, p3.y - p2.y];

                            let angle = Math.acos(
                                (p21[0] * p23[0] + p21[1] * p23[1])
                                /
                                (
                                    Math.sqrt(p21[0] * p21[0] + p21[1] * p21[1])
                                    * Math.sqrt(p23[0] * p23[0] + p23[1] * p23[1])
                                )
                            );

                            if (angle < minimumRadianAngle) {
                                pol.triangulation = null;

                                if (Object.keys(this.packing.rGraph[[p1.x, p1.y]]).length < Object.keys(this.packing.rGraph[[p3.x, p3.y]]).length) {
                                    deletedPoints.push(p1);
                                } else {
                                    deletedPoints.push(p3);
                                }
                                changed = true;
                            }
                        }

                        deletedPoints.forEach(point => {
                            this.packing.polygons.map(pol => {
                                pol.points = pol.points.filter(pnt => !(pnt.x === point.x && pnt.y === point.y));
                            });
                        })
                    }
                });

                if (changed) {
                    this.triangulateMesh();
                }

                this.parseMesh(this.packing);
                this.dialogAngle = false;
            },
            loadOriginal() {
                this.packing.polygons = JSON.parse(JSON.stringify(this.packing.originalPacking.polygons));
                this.triangulateMesh();
                this.parseMesh(this.packing);
                this.dialogAngle = false;
            },
            triangulateMesh() {
                this.packing.polygons.forEach(pol => {
                    pol.triangulation = [];
                    let contour = [];
                    pol.points.forEach(pnt => {
                        contour.push(new poly2tri.Point(pnt.x, pnt.y))
                    });
                    let swctx = new poly2tri.SweepContext(contour);
                    try {
                        swctx.triangulate();
                    } catch (error) {
                        console.error(error);
                    }

                    let triangles = swctx.getTriangles();
                    triangles.forEach(function (t) {
                        let triangle = [];
                        t.getPoints().forEach(function (p) {
                            triangle.push({x: p.x, y: p.y});
                        });

                        pol.triangulation.push(triangle);
                    });
                })
            },
            importPacking() {
                this.$refs.refImportPacking.openDialog();
            },
            exportPacking() {
                this.$refs.refExportPacking.openDialog();
            },
            assignProperties(selectedOptionProperties, selectedOptionType) {
                let polygons = this.packing.polygons;
                let draw = Object.assign({}, this.packing.draw);
                polygons.forEach(polygon => {
                    if ((selectedOptionProperties.value === 0 && polygon.selected) || selectedOptionProperties.value === 1) {
                        if (selectedOptionType.value === 0
                            || (selectedOptionType.value === 1 && !polygon.hole)
                            || (selectedOptionType.value === 2 && polygon.hole)) {
                            if (polygon.triangulation == null) {
                                polygon.triangulation = [];
                                let contour = [];
                                polygon.points.forEach(pnt => {
                                    contour.push(new poly2tri.Point(pnt.x, pnt.y))
                                });
                                let swctx = new poly2tri.SweepContext(contour);
                                swctx.triangulate();
                                let triangles = swctx.getTriangles();
                                triangles.forEach(function (t) {
                                    let triangle = [];
                                    t.getPoints().forEach(function (p) {
                                        triangle.push({x: p.x, y: p.y});
                                    });

                                    polygon.triangulation.push(triangle);
                                });
                            }

                            polygon.properties = [];
                            let properties = this.properties;
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    polygon.properties.push({key: item, value: properties[item].default})
                                }
                            });

                            let polygonPoints = [];
                            for (let i = 0; i < polygon.points.length; i++) {
                                let pointA = polygon.points[i];
                                polygonPoints.push(this.packing.draw.points[[pointA.x, pointA.y]]);
                            }

                            draw.polygons[polygonPoints].polygon.properties = polygon.properties;
                        }
                    }
                });

                this.$store.commit("assignProperties", {
                    draw: draw,
                    polygons: polygons
                });

                this.ps.draw();
                this.dialog2 = false;

                //this.snackbar = true;
                //this.snackbarMessage = "Properties assigned successfully";
            },
            drawPolygon(polygon, width, height, p) {
                p.stroke(33, 33, 33);
                p.strokeWeight(1);
                if (polygon.properties != null && polygon.properties.length > 0) {
                    if (polygon.triangulation != null && polygon.triangulation.length >= polygon.properties.length) {
                        let properties = this.properties;
                        polygon.properties = polygon.properties.filter(prop => Object.keys(properties).includes(prop.key));

                        if (polygon.properties.length > 0) {
                            let painted_triangles_each_step = Math.floor(polygon.triangulation.length / polygon.properties.length);
                            for (let i = 0; i < polygon.triangulation.length; i += 1) {
                                p.fill(properties[polygon.properties[(Math.floor(i / painted_triangles_each_step)) % polygon.properties.length].key].color);
                                p.stroke(properties[polygon.properties[(Math.floor(i / painted_triangles_each_step)) % polygon.properties.length].key].color);
                                p.beginShape();
                                polygon.triangulation[i].forEach(pnt => {
                                    let sx = ((pnt.x / width) * this.getWidth(p)) + this.getOffsetXAxis();
                                    let sy = (((height - pnt.y) / height) * this.getHeight(p)) + + this.getOffsetYAxis();

                                    p.vertex(sx, sy);
                                });
                                p.endShape(p.CLOSE);
                            }
                        }
                    }
                }
                if(polygon.selected) {
                    p.fill('rgba(0,0,0, 0.25)');
                    p.beginShape();
                    polygon.points.forEach(pnt => {
                        p.vertex(
                            ((pnt.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                            (((height - pnt.y) / height) * this.getHeight(p)) + + this.getOffsetYAxis()
                        );
                    });
                    p.endShape(p.CLOSE);
                }
            },
            execute() {
                this.$validator.validateAll().then(result => {
                    if (result) {
                        if (this.polygons.length === 0) {
                            alert('Must insert at least one polygon')
                        } else {

                            let data = {
                                'polygons': this.polygons.map(x => {
                                    return {
                                        'label': x.label,
                                        'numberOfVertex': parseInt(x.numberOfVertex),
                                        'percentage': parseInt(x.percentage),
                                        'radius': parseFloat(x.radius)
                                    };
                                }),
                                'width': parseFloat(this.width),
                                'height': parseFloat(this.height),
                                'randomShape': this.randomShape,
                                'regularity': parseInt(180 / parseInt(this.regularity)),
                                'approachAlgorithm': this.approach
                            };
                            this.executing = true;
                            api.sendMesh(data).then(resp => {
                                this.executing = false;
                                this.$store.commit("newPacking", resp.body.mesh);
                                this.parseMesh(resp.body.mesh);
                                this.$refs.boundaryConditionsComponent.updatePacking();
                            }).catch(error => {
                                this.executing = false;
                                console.log(error);
                                alert("Error executing algorithm.");
                            });
                        }
                        this.dialog = false;
                    }
                });
            },
            executeMultiLayer() {
                this.$validator.validateAll().then(result => {
                    if (result) {
                        if (this.polygons.length === 0) {
                            alert('Must insert at least one polygon')
                        } else {

                            let data = {
                                'layers': this.layers.map(lay => {
                                    return {
                                        'height': parseFloat(lay.height),
                                        'regularity': parseInt(180 / parseInt(lay.regularity)),
                                        'polygons': lay.polygons.map(x => {
                                            return {
                                                'label': x.label,
                                                'numberOfVertex': parseInt(x.numberOfVertex),
                                                'percentage': parseInt(x.percentage),
                                                'radius': parseFloat(x.radius)
                                            };
                                        }),
                                    };
                                }),
                                'width': parseFloat(this.width),
                                'randomShape': this.randomShape,
                                'regularity': parseInt(180 / parseInt(this.regularity)),
                                'approachAlgorithm': this.approach
                            };

                            this.executing = true;
                            api.sendMeshMultiLayers(data).then(resp => {
                                this.executing = false;
                                this.$store.commit("newPacking", resp.body.mesh);
                                this.parseMesh(resp.body.mesh);
                                this.$refs.boundaryConditionsComponent.updatePacking();
                            }).catch(error => {
                                this.executing = false;
                                console.log(error);
                                alert("Error executing algorithm.");
                            });
                        }
                        this.dialog = false;
                    }
                });
            },
            loadTxtPacking(data) {
                this.$store.commit("newPacking", data);
                this.parseMesh(data);
                this.$refs.boundaryConditionsComponent.updatePacking();
            },
            parseMesh(mesh) {
                //console.log(resp);

                const width = mesh.width;
                const height = mesh.height;

                let points = {};
                let edges = {};
                let polygons = {};
                let borderPoints = {};
                let borderSegments = {};
                let edgesG = {};
                let cEdgesG = {};
                let p = 1;
                let e = 1;
                let bp = 1;
                let bs = 1;
                let polCount = 1;

                function checkEpsilon(value, equalValue, precision = 1e-8) {
                    return Math.abs(value - equalValue) < precision;
                }

                function isBorderPoint(point, minX, maxX, minY, maxY) {
                    return !!(checkEpsilon(point.x, minX)
                        || checkEpsilon(point.x, maxX)
                        || checkEpsilon(point.y, minY)
                        || checkEpsilon(point.y, maxY));

                }

                function isBorderSegmentSlope(pointA, pointB) {
                    if(Math.abs(pointA.y - pointB.y) < 1e-8) return true;
                    return Math.abs(pointA.x - pointB.x) < 1e-8;

                }

                function checkBorderPoint(point, borderPoints, width, height, bpCount, polCount) {
                    let nPoint = false;
                    // Check if the point is on the border of the container.
                    if (isBorderPoint(point, 0, width, 0, height)) {

                        // Check if the point is already labeled.
                        if (!([point.x, point.y] in borderPoints)) {
                            borderPoints[[point.x, point.y]] = {
                                index: bpCount,
                                pointIndex: points[[point.x, point.y]],
                                polygons: []
                            };
                            nPoint = true;
                        }

                        // Add the polygon if needed.
                        if (!borderPoints[[point.x, point.y]].polygons.includes(polCount)) {
                            borderPoints[[point.x, point.y]].polygons.push(polCount);
                        }
                    }
                    return nPoint;
                }

                function checkBorderSegment(pointA, pointB, borderSegments, width, height, bsCount, polCount) {
                    let nSegment = false;
                    // Check if one of the points is on the border of the container.
                    if (isBorderPoint(pointA, 0, width, 0, height)
                        && isBorderPoint(pointB, 0, width, 0, height)
                        && isBorderSegmentSlope(pointA, pointB) ) {

                        // Check if the segment is already labeled.
                        if (!([points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]] in borderSegments)) {
                            borderSegments[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]] = {
                                index: bsCount,
                                segmentIndex: edges[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]],
                                polygons: []
                            };
                            nSegment = true;
                        }

                        // Add the polygon if needed.
                        if (!borderSegments[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]].polygons.includes(polCount)) {
                            borderSegments[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]].polygons.push(polCount);
                        }
                    }
                    return nSegment;
                }

                mesh.polygons.forEach(pol => {

                    let polygonPoints = [];

                    for (let i = 0; i < pol.points.length; i++) {

                        let pointA = pol.points[i];
                        let pointB = pol.points[(i + 1) % pol.points.length];

                        if (!([pointA.x, pointA.y] in points)) {
                            points[[pointA.x, pointA.y]] = p;
                            p += 1;
                        }

                        if (!([pointB.x, pointB.y] in points)) {
                            points[[pointB.x, pointB.y]] = p;
                            p += 1;
                        }

                        // Check if the points are on the border of the container.
                        if (checkBorderPoint(pointA, borderPoints, width, height, bp, polCount)) {
                            bp += 1
                        }
                        if (checkBorderPoint(pointB, borderPoints, width, height, bp, polCount)) {
                            bp += 1
                        }

                        if (!([points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]] in edges)) {
                            edges[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]] = e;

                            // Check if the segment is on the border of the container.
                            if (checkBorderSegment(pointA, pointB, borderSegments, width, height, bs, polCount)) {
                                bs += 1
                            }

                            e += 1;

                            if (!([pointA.x, pointA.y] in cEdgesG)) {
                                cEdgesG[[pointA.x, pointA.y]] = {};
                            }
                            if (!([pointB.x, pointB.y] in cEdgesG)) {
                                cEdgesG[[pointB.x, pointB.y]] = {};
                            }

                            cEdgesG[[pointA.x, pointA.y]][[pointB.x, pointB.y]] = {};
                            cEdgesG[[pointB.x, pointB.y]][[pointA.x, pointA.y]] = {};

                            if (pol.hole) {
                                if (!([pointA.x, pointA.y] in edgesG)) {
                                    edgesG[[pointA.x, pointA.y]] = {};
                                }
                                edgesG[[pointA.x, pointA.y]][[pointB.x, pointB.y]] = {
                                    selected: false,
                                    hover: false,
                                };
                            }
                        }
                        polygonPoints.push(points[[pointA.x, pointA.y]]);
                    }

                    if (!(polygonPoints in polygons)) {
                        polygons[polygonPoints] = {
                            count: polCount,
                            polygon: pol
                        };
                        polCount += 1;
                    }
                });

                this.$store.commit("updatePacking", {
                    points: points,
                    edges: edges,
                    polygons: polygons,
                    borderPoints: borderPoints,
                    borderSegments: borderSegments,
                    edgesG: edgesG,
                    cEdgesG: cEdgesG,
                });
            },
            downloadImage() {
                let filename = 'packing.png';
                this.ps.save(filename);
            },
            polygonIntersection(polygon, box, width, height, p) {
                let intersects = false;
                for (let i = 0; i < polygon.points.length; i++) {

                    let pntA = polygon.points[i];
                    let pntB = polygon.points[(i + 1) % polygon.points.length];
                    let xi = ((pntA.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yi = (((height - pntA.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();
                    let xj = ((pntB.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yj = (((height - pntB.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();
                    for (let j = 0; j < box.points.length; j++) {
                        let pntC = box.points[j];
                        let pntD = box.points[(j + 1) % box.points.length];
                        intersects = intersects || this.vectorIntersection(xi, yi, xj, yj, pntC.x, pntC.y, pntD.x, pntD.y);
                    }
                }

                if (!intersects) {
                    let pntA = polygon.points[0];
                    let x = ((pntA.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        y = (((height - pntA.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();
                    let inside = false;
                    for (let i = 0; i < box.points.length; i++) {
                        let xi = box.points[i].x,
                            yi = box.points[i].y;
                        let xj = box.points[(i + 1) % box.points.length].x,
                            yj = box.points[(i + 1) % box.points.length].y;

                        let intersect = ((yi > y) !== (yj > y))
                            && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
                        if (intersect) inside = !inside;
                    }
                    if (inside) intersects = true;
                }
                return intersects;
            },
            vectorIntersection(a, b, c, d, p, q, r, s) {
                let det, gamma, lambda;

                det = (c - a) * (s - q) - (r - p) * (d - b);
                if (det === 0) {
                    return false;
                } else {
                    lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
                    gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
                    return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
                }
            },
            openAssignProp() {
                this.dialog2 = true;
            },
            addLayer() {
                this.layers.push({
                    height: null,
                    regularity: 5
                });
            },
            deleteLayer() {
                this.layers.pop();
            }
        },
    }
</script>

<style scoped>
    .page-container {
        padding: 0;
        margin: 0;
        height: 100%;
    }

    .polygon {
        padding: 0;
        margin: 0;
        height: 90%;
        min-width: 100%;
    }

    .my-card {
        height: 100%;
    }

    .all-height {
        height: 100%;
    }

    .max-height-card {
        max-height: 600px;
        overflow: auto;
    }
</style>