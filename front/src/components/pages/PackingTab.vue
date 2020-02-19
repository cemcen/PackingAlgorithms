<template>
    <v-row style="height: 90vh">
        <v-col sm="9" class="pl-9 pt-0" style="height: 90vh">
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
                    <!--<v-tooltip top>
                        <template v-slot:activator="{ on }">
                            <v-btn icon text color="teal lighten-2" :disabled="executing" v-on="on"
                                   @click="openAngleDialog()">
                                <v-icon>mdi-angle-acute</v-icon>
                            </v-btn>
                        </template>
                        <span>Minimum Angle</span>
                    </v-tooltip>-->
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

                    <boundary-conditions ref="boundaryConditionsComponent" :dialog="dialogBoundaryConditions" :polygon-shape="polygonsShape"
                                         @changedBoundary="changedBoundary" @loadOriginal="loadOriginal"
                                         @closeDialog="closedDialog()"/>
                    <import-packing ref="refImportPacking" @loadtxtpacking="loadTxtPacking" @loadJSON="loadedJSON"
                                    @closedDialog="closedDialog()"/>
                    <download-packing ref="refExportPacking" @closedDialog="closedDialog()"/>
                    <upload-results-dialog ref="uploadResultsRef" @closedDialog="closedDialog()"/>

                </v-toolbar>
                <div id='myContainer' ref="polygonContainer" class="polygon">
                    <div ref="polygonDrawer"></div>
                </div>
            </v-card>
        </v-col>
        <v-col sm="3" class="pt-0">
            <v-card class="my-card">
                <v-tabs fixed-tabs color="teal lighten-2">
                    <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

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
        </v-col>
    </v-row>
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

            this.loadMesh();

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
                    if(this.layer != null) {
                        this.layer.destroyChildren();
                    } else {
                        this.layer = new Konva.Layer();
                    }

                    this.polygonsShape = [];
                    this.packing.polygons.forEach(pol => {
                        this.polygonsShape.push(new Polygon(pol, this.packing.width, this.packing.height, this.stage, this.layer, this.properties));
                    });

                    this.dragBox = new DragBox(this.polygonsShape, this.packing.width, this.packing.height, this.stage, this.layer);
                    this.stage.add(this.layer);
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

                    this.createPackingPolygons();
                    this.dialogAngle = false;
                }
            },
            loadOriginal() {
                this.executing = true;
                let packing = JSON.parse(JSON.stringify(this.packing.originalPacking));
                this.$store.commit('newPacking', packing);
                this.triangulateMesh();
                this.createPackingPolygons();
                this.findBoundaryElements();
                this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.uploadResultsRef.createNewPacking();
                this.executing = false;
            },
            loadMesh() {
                this.createPackingPolygons();
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

                this.openedDialog = false;
                this.dialog2 = false;
            },
            updatePacking(resp) {
                this.executing = false;
                let packing = this.triangulatePacking(resp.body.mesh);
                this.$store.commit("newPacking", packing);
                this.findBoundaryElements();
                this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.uploadResultsRef.createNewPacking();
                this.createPackingPolygons();
                this.openedDialog = false;
            },
            findBoundaryElements() {
                let segmentsCount = {};
                this.packing.polygons.forEach((pol, index) => {
                    for(let i = 0; i < pol.points.length; i++) {
                        let pntA = pol.points[i];
                        let pntB = pol.points[(i + 1) % pol.points.length];

                        let x1 = parseFloat(pntA.x.toFixed(6));
                        let y1 = parseFloat(pntA.y.toFixed(6));
                        let x2 = parseFloat(pntB.x.toFixed(6));
                        let y2 = parseFloat(pntB.y.toFixed(6));

                        if ([x1, y1] in segmentsCount && [x2, y2] in segmentsCount[[x1, y1]]) {
                            segmentsCount[[x1, y1]][[x2, y2]].count += 1;
                        } else if ([x2, y2] in segmentsCount && [x1, y1] in segmentsCount[[x2, y2]]) {
                            segmentsCount[[x2, y2]][[x1, y1]].count += 1;
                        } else {
                            if(!([x1, y1] in segmentsCount)) {
                                segmentsCount[[x1, y1]] = {};
                            }
                            segmentsCount[[x1, y1]][[x2, y2]] = {
                                count: 1,
                                pntA: pntA,
                                pntB: pntB,
                                polIndex: index
                            };
                        }
                    }
                });

                let borderSegments = [];
                let borderPoints = [];
                Object.keys(segmentsCount).forEach(k => {
                    Object.keys(segmentsCount[k]).forEach(k2 => {
                        if(segmentsCount[k][k2].count === 1) {
                            borderSegments.push({
                                pntA: segmentsCount[k][k2].pntA,
                                pntB: segmentsCount[k][k2].pntB,
                                polIndex: segmentsCount[k][k2].polIndex,
                            });

                            if(!borderPoints.includes(segmentsCount[k][k2].pntA)) borderPoints.push(segmentsCount[k][k2].pntA);
                            if(!borderPoints.includes(segmentsCount[k][k2].pntB)) borderPoints.push(segmentsCount[k][k2].pntB);
                        }
                    });
                });

                this.$store.commit("borderElements", {segments: borderSegments, points: borderPoints});

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
                //this.parseMesh(this.packing);
                //this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.uploadResultsRef.createNewPacking();
            },
            loadTxtPacking(data) {
                this.$store.commit("newPacking", data);
                this.findBoundaryElements();
                this.$refs.boundaryConditionsComponent.updatePacking();
                //this.$refs.uploadResultsRef.createNewPacking();
                this.createPackingPolygons();
                this.openedDialog = false;
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
                let dataURL = this.stage.toCanvas({pixelRatio: 3});
                dataURL.toBlob((blob) => {
                    this.downloadFile(blob, filename, 'png');
                });
            },
            openAssignProp() {
                this.dialog2 = true;
                this.openedDialog = true;
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
                this.findBoundaryElements();
                this.$refs.boundaryConditionsComponent.updatePacking();
                this.createPackingPolygons();
                //this.$refs.uploadResultsRef.createNewPacking();
                this.openedDialog = false;
                this.executing = false;
            },
            loadedJSON() {
                this.openedDialog = false;
                this.drawPacking = true;
            }
        },
    }
</script>

<style scoped>

    .polygon {
        padding: 0;
        margin: 0;
        height: 90%;
        min-width: 100%;
    }

    .my-card {
        height: 90vh;
    }

    .max-height-card {
        max-height: 600px;
        overflow: auto;
    }
</style>