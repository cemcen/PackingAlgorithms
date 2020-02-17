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
                                <v-btn color="teal lighten-2" text :disabled="executing" @click="openNewPackingDialog()"
                                       icon
                                       v-on="on">
                                    <v-icon>mdi-plus-circle</v-icon>
                                </v-btn>
                            </template>
                            <span>Create New Packing</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn color="teal lighten-2" v-on="on" :disabled="executing" icon text
                                       @click.native="loadOriginal">
                                    <v-icon>mdi-backup-restore</v-icon>
                                </v-btn>
                            </template>
                            <span>Load Original Packing</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                       @click="openAssignProp">
                                    <v-icon>mdi-palette</v-icon>
                                </v-btn>
                            </template>
                            <span>Assign Properties</span>
                        </v-tooltip>

                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                       @click="exportPacking">
                                    <v-icon>mdi-download</v-icon>
                                </v-btn>
                            </template>
                            <span>Export Mesh File</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                       @click="importPacking">
                                    <v-icon>mdi-upload</v-icon>
                                </v-btn>
                            </template>
                            <span>Upload Mesh</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                       @click="downloadImage">
                                    <v-icon>mdi-file-image</v-icon>
                                </v-btn>
                            </template>
                            <span>Download Image</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                       @click="openAngleDialog()">
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

                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn color="teal lighten-2" v-on="on" :disabled="executing" icon text
                                       @click.native="uploadResults">
                                    <v-icon>mdi-chart-bar</v-icon>
                                </v-btn>
                            </template>
                            <span>Upload Results</span>
                        </v-tooltip>

                        <v-tooltip top>
                            <template v-slot:activator="{ on }">
                                <v-btn color="teal lighten-2" v-on="on" :disabled="executing" icon text
                                       @click.native="convertMeshToAllConvexPolygons">
                                    <v-icon>mdi-shape-polygon-plus</v-icon>
                                </v-btn>
                            </template>
                            <span>Convert All Polygons to Convex</span>
                        </v-tooltip>

                        <v-spacer></v-spacer>

                        <v-progress-circular v-show="executing" indeterminate
                                             color="teal lighten-2"></v-progress-circular>

                        <dialog-new-packing :dialog="dialog" @close="closedDialog()"
                                            @execute="execute" @execute-multi-layer="executeMultiLayer"/>

                        <assign-properties :dialog="dialog2"
                                           @closeDialog="closedDialog"
                                           @assignProperties="assignProperties"/>

                        <v-dialog eager v-model="dialogAngle" persistent max-width="500px">
                            <v-card>
                                <v-card-title>
                                    <span class="headline">Minimum Layout Angle</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-form ref="minimumAngleForm">
                                        <v-row justify="center">
                                            <v-col class="pa-0 pr-3 pl-3">
                                                <v-text-field v-model="minimumAngle" label="Minimum Angle"
                                                              :rules="[validation.required(), validation.requiredPositive()]"
                                                              type="number" clearable required>
                                                </v-text-field>
                                            </v-col>
                                        </v-row>
                                    </v-form>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" text @click.native="closedDialog()">Close
                                    </v-btn>
                                    <v-btn dark color="teal lighten-2" @keyup.enter="optimizeAngle"
                                           @click.native="optimizeAngle">Optimize
                                    </v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <boundary-conditions ref="boundaryConditionsComponent" :dialog="dialogBoundaryConditions"
                                             @changedBoundary="changedBoundary" @loadOriginal="loadOriginal"
                                             @closeDialog="closedDialog()"/>
                        <import-packing ref="refImportPacking" @loadtxtpacking="loadTxtPacking" @loadJSON="loadedJSON"
                                        @closedDialog="closedDialog()"/>
                        <download-packing ref="refExportPacking" @closedDialog="closedDialog()"/>
                        <upload-results-dialog ref="uploadResultsRef" @closedDialog="closedDialog()"/>

                    </v-toolbar>
                    <div id='myContainer' ref="polygonContainer" class="polygon" style="height: 95%; width: 100%">
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
                            <v-icon class="mr-1">mdi-shape-plus</v-icon>
                            Polygons
                        </v-tab>

                        <v-tab id="tab-distribution" @click="selectTab(0)">
                            <v-icon class="mr-1">mdi-clipboard-text</v-icon>
                            Properties
                        </v-tab>
                    </v-tabs>
                    <div v-show="selectedTab === 0" class="max-height-card">
                        <properties-tab ref="propertiesTab"></properties-tab>
                    </div>

                    <div v-show="selectedTab === 1" class="max-height-card">
                        <polygons-tab></polygons-tab>
                    </div>
                </v-card>
            </v-flex>
        </v-layout>
    </div>
</template>

<script>
    import PropertiesTab from "./PropertiesTab.vue";
    import PolygonsTab from "./PolygonsTab.vue";
    import api from "../../services/api.services";
    import * as poly2tri from 'poly2tri';
    import Swatches from 'vue-swatches';
    import validation from './../../services/validation.service';

    import "vue-swatches/dist/vue-swatches.min.css"
    import BoundaryConditions from "../templates/BoundaryConditions.vue";
    import AssignProperties from "../templates/AssignProperties.vue";
    import ImportPacking from "../templates/ImportPacking.vue";
    import DownloadPacking from "../templates/DownloadPacking.vue";
    import DialogNewPacking from "../templates/DialogNewPacking.vue";
    import UploadResultsDialog from "../templates/UploadResultsDialog.vue";
    import Polygon from "../geometry/konvas/polygon";
    import DragBox from "../geometry/konvas/dragBox";

    const routes = ["/properties", "/polygons", "/info"];

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        components: {
            UploadResultsDialog,
            DialogNewPacking,
            DownloadPacking,
            ImportPacking,
            AssignProperties,
            BoundaryConditions,
            PolygonsTab,
            PropertiesTab,
            Swatches,
        },
        name: "packing",
        data() {
            return {
                validation: validation,
                openedDialog: false,
                drawSelectionBox: false,
                drawPacking: true,
                selectedTab: 1,
                stage: null,
                layer: null,
                dragBox: null,
                polygonsShape: [],
                dialogBoundaryConditions: false,
                timeout: 1200,
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
            this.validation.changeLanguage('en');
        },
        computed: {
            getSelectedPolygons() {
                return (this.packing && this.packing.polygons) ? this.packing.polygons.filter(pol => pol.selected) : [];
            },
            properties() {
                return this.$store.getters.getProperties;
            },
            polygons() {
                return this.$store.getters.getPolygons;
            },
            packing() {
                return this.$store.getters.getPacking;
            }
        },
        mounted() {

            this.stage = new Konva.Stage({
                container: 'myContainer',
                width: 100,
                height: 100
            });

            this.createPackingPolygons();

            // adapt the stage on any window resize
            window.addEventListener('resize', () => {
                let container = this.$refs.polygonContainer;

                this.stage.height(container.clientHeight);
                this.stage.width(container.clientWidth);
                this.stage.draw();
            });
            window.dispatchEvent(new Event('resize'));
        },
        methods: {
            createPackingPolygons() {
                if (this.packing && this.packing.polygons) {
                    this.stage.destroyChildren();
                    let layer = new Konva.Layer();
                    this.polygonsShape = [];
                    this.packing.polygons.forEach(pol => {
                        this.polygonsShape.push(new Polygon(pol, this.packing.width, this.packing.height, this.stage, layer, this.properties));
                    });
                    this.dragBox = new DragBox(this.polygonsShape, this.packing.width, this.packing.height, this.stage, layer);
                    this.layer = layer;
                    this.stage.add(layer);
                }
            },
            selectTab(i) {
                this.selectedTab = i;
                this.$router.push(routes[i]);
            },
            openNewPackingDialog() {
                this.dialog = true;
                this.openedDialog = true;
            },
            closedDialog() {
                this.dialog = false;
                this.dialog2 = false;
                this.dialogAngle = false;
                this.dialogBoundaryConditions = false;
                this.openedDialog = false;
            },
            openAngleDialog() {
                this.$refs.minimumAngleForm.resetValidation();
                this.dialogAngle = true;
                this.openedDialog = true;
            },
            openBorderConditions() {
                this.dialogBoundaryConditions = true;
                this.openedDialog = true;
                this.$refs.boundaryConditionsComponent.reDraw();
            },
            optimizeAngle() {
                if (this.$refs.minimumAngleForm.validate()) {
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
                }
            },
            loadOriginal() {
                this.executing = true;
                this.packing.polygons = JSON.parse(JSON.stringify(this.packing.originalPacking.polygons));
                this.triangulateMesh();
                this.parseMesh(this.packing);
                this.$refs.boundaryConditionsComponent.updatePacking();
                this.$refs.uploadResultsRef.createNewPacking();
                this.drawPacking = true;
                this.ps.draw();
                this.executing = false;
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
                this.openedDialog = true;
            },
            exportPacking() {
                this.$refs.refExportPacking.openDialog();
                this.openedDialog = true;
            },
            assignProperties(selectedOptionProperties, selectedOptionType) {
                let polygons = this.polygonsShape;
                let draw = Object.assign({}, this.packing.draw);
                polygons.forEach(polygon => {
                    if ((selectedOptionProperties.value === 0 && polygon.isSelected()) || selectedOptionProperties.value === 1) {
                        if (selectedOptionType.value === 0
                            || (selectedOptionType.value === 1 && !polygon.isHole())
                            || (selectedOptionType.value === 2 && polygon.isHole())) {

                            let assignProperties = [];
                            let properties = this.properties;
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    assignProperties.push({key: item, value: properties[item].default})
                                }
                            });

                            polygon.assignProperties(assignProperties, this.properties);
                        }
                    }
                });
                this.layer.draw();

                // LEGACY
                polygons = this.packing.polygons;
                polygons.forEach(polygon => {
                    if ((selectedOptionProperties.value === 0 && polygon.selected) || selectedOptionProperties.value === 1) {
                        if (selectedOptionType.value === 0
                            || (selectedOptionType.value === 1 && !polygon.hole)
                            || (selectedOptionType.value === 2 && polygon.hole)) {
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

                this.openedDialog = false;
                this.dialog2 = false;
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
                                    let sy = (((height - pnt.y) / height) * this.getHeight(p)) + +this.getOffsetYAxis();

                                    p.vertex(sx, sy);
                                });
                                p.endShape(p.CLOSE);
                            }
                        }
                    }
                }
                if (polygon.selected) {
                    p.fill('rgba(0,0,0, 0.25)');
                    p.beginShape();
                    polygon.points.forEach(pnt => {
                        p.vertex(
                            ((pnt.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                            (((height - pnt.y) / height) * this.getHeight(p)) + +this.getOffsetYAxis()
                        );
                    });
                    p.endShape(p.CLOSE);
                }
            },
            updatePacking(resp) {
                this.executing = false;
                let packing = this.triangulatePacking(resp.body.mesh);
                this.$store.commit("newPacking", packing);
                this.parseMesh(packing);
                //this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.uploadResultsRef.createNewPacking();
                this.createPackingPolygons();
                this.openedDialog = false;
            },
            execute(packingOptions) {
                if (this.polygons.length === 0) {
                    this.$toast('Must insert at least one polygon');
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
                        'width': parseFloat(packingOptions.width),
                        'height': parseFloat(packingOptions.height),
                        'randomShape': packingOptions.randomShape,
                        'regularity': parseInt(180 / parseInt(packingOptions.regularity)),
                        'approachAlgorithm': packingOptions.approach
                    };
                    this.executing = true;
                    api.sendMesh(data).then(resp => {
                        this.updatePacking(resp);
                    }).catch(error => {
                        this.executing = false;
                        console.log(error);
                        this.$toast("Error executing algorithm.");
                    });
                }
                this.dialog = false;
            },
            executeMultiLayer(packingOptions) {
                if (this.polygons.length === 0) {
                    this.$toast('Must insert at least one polygon');
                } else {

                    let data = {
                        'layers': packingOptions.layers.map(lay => {
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
                        'width': parseFloat(packingOptions.width),
                        'randomShape': packingOptions.randomShape,
                        'regularity': parseInt(180 / parseInt(packingOptions.regularity)),
                        'approachAlgorithm': packingOptions.approach
                    };

                    this.executing = true;
                    api.sendMeshMultiLayers(data).then(resp => {
                        this.updatePacking(resp);
                    }).catch(error => {
                        this.executing = false;
                        console.log(error);
                        this.$toast("Error executing algorithm.");
                    });
                }
                this.dialog = false;
            },
            triangulatePacking(mesh) {
                mesh.polygons.forEach(pol => {
                    pol.triangulation = [];
                    let contour = [];
                    pol.points.forEach(pnt => {
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

                        pol.triangulation.push(triangle);
                    });
                });
                return mesh;
            },
            changedBoundary() {
                this.parseMesh(this.packing);
                this.$refs.boundaryConditionsComponent.updatePacking();
                this.$refs.uploadResultsRef.createNewPacking();
            },
            loadTxtPacking(data) {
                this.$store.commit("newPacking", data);
                this.parseMesh(data);
                this.$refs.boundaryConditionsComponent.updatePacking();
                this.$refs.uploadResultsRef.createNewPacking();
                this.drawPacking = true;
                this.ps.draw();
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
                    if (Math.abs(pointA.y - pointB.y) < 1e-8) return true;
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

                function checkBorderSegment(pointA, pointB, borderSegments, width, height, bsCount, polCount, indexPol) {
                    let nSegment = false;
                    // Check if one of the points is on the border of the container.
                    if (isBorderPoint(pointA, 0, width, 0, height)
                        && isBorderPoint(pointB, 0, width, 0, height)
                        && isBorderSegmentSlope(pointA, pointB)) {

                        // Check if the segment is already labeled.
                        if (!([points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]] in borderSegments)) {
                            borderSegments[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]] = {
                                index: bsCount,
                                segmentIndex: edges[[points[[pointA.x, pointA.y]], points[[pointB.x, pointB.y]]]],
                                polygons: [],
                                indexPol: indexPol
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

                mesh.polygons.forEach((pol, indexPol) => {

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
                            if (checkBorderSegment(pointA, pointB, borderSegments, width, height, bs, polCount, indexPol)) {
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
            downloadFile(blob, filename, type) {
                const e = document.createEvent('MouseEvents'),
                    a = document.createElement('a');
                a.download = filename;
                a.href = window.URL.createObjectURL(blob);
                a.dataset.downloadurl = [type, a.download, a.href].join(':');
                e.initEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                a.dispatchEvent(e);
            },
            downloadImage() {
                let filename = 'results.png';
                let dataURL = this.stage.toCanvas({ pixelRatio: 3 });
                dataURL.toBlob((blob) => {
                    this.downloadFile(blob,filename, 'png');
                });
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
                this.openedDialog = true;
            },
            pointInsidePolygon(polygon, mousePoint, width, height, p) {
                let intersections = 0;
                for (let i = 0; i < polygon.points.length; i++) {

                    let pntA = polygon.points[i];
                    let pntB = polygon.points[(i + 1) % polygon.points.length];
                    let xi = ((pntA.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yi = (((height - pntA.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();
                    let xj = ((pntB.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yj = (((height - pntB.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();

                    if (this.vectorIntersection(xi, yi, xj, yj, mousePoint[0], mousePoint[1], -1000, -1000)) {
                        intersections += 1;
                    }
                }

                return intersections % 2 !== 0;
            },
            uploadResults() {
                this.openedDialog = true;
                this.$refs.uploadResultsRef.openDialog();
            },
            convertMeshToAllConvexPolygons() {
                this.executing = true;
                let packingToChange = JSON.parse(JSON.stringify(this.packing));
                let nPacking = {
                    polygons: [],
                    width: packingToChange.width,
                    height: packingToChange.height
                };
                let decomp = require('poly-decomp');
                try {
                    packingToChange.polygons.forEach(pol => {
                        if (pol.hole) {
                            let concavePol = pol.points.map(pnt => [pnt.x, pnt.y]);
                            decomp.makeCCW(concavePol);
                            let convexPolygons = decomp.quickDecomp(concavePol);
                            convexPolygons.forEach(cnx => {
                                nPacking.polygons.push({
                                    label: '',
                                    radius: -1,
                                    hole: pol.hole,
                                    area: pol.area,
                                    points: cnx.map(pnt => {
                                        return {'x': pnt[0], 'y': pnt[1]};
                                    })
                                })
                            });
                        } else {
                            nPacking.polygons.push(pol);
                        }
                    });
                } catch (e) {
                    console.log(e);
                    this.executing = false;
                }
                let packing = this.triangulatePacking(nPacking);
                this.$store.commit('updateOnlyPackingPolygons', packing.polygons);
                this.parseMesh(packing);
                this.$refs.boundaryConditionsComponent.updatePacking();
                this.$refs.uploadResultsRef.createNewPacking();
                this.drawPacking = true;
                this.openedDialog = false;
                this.executing = false;
                this.ps.draw();
            },
            loadedJSON() {
                this.openedDialog = false;
                this.drawPacking = true;
                this.ps.draw();
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