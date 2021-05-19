<template>
    <v-row style="height: 90vh; margin: 0">
        <v-col sm="8" class="pl-9 pt-0" style="height: 90vh">
            <v-card class="my-card">
                <div id='myContainer' ref="polygonContainer" class="polygon">
                    <div ref="polygonDrawer"></div>
                </div>
            </v-card>
        </v-col>

        <v-col sm="4" class="pt-0 pr-9">
            <v-card class="my-card">
                <v-tabs fixed-tabs color="teal lighten-2">
                    <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

                    <v-tab v-for="(cat, index) in categories" :key="index" @click="selectTab(cat.tab, cat)">
                        <v-icon class="mr-1">{{cat.icon}}</v-icon>
                        <span>{{cat.name}}</span>
                    </v-tab>
                </v-tabs>
                <v-row class="pa-0 ma-0" v-for="(cat, index) in categories" :key="index" v-show="selectedTab === cat.tab">
                    <v-tabs fixed-tabs color="teal lighten-2">
                        <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

                        <v-tab v-for="(cat2, index2) in cat.subcategories" :key="index2"
                               @click="selectSubTab(cat2.tab, cat)">
                            <v-tooltip top>
                                <template v-slot:activator="{ on }">
                                    <v-icon v-on="on">{{cat2.icon}}</v-icon>
                                </template>
                                <span>{{cat2.name}}</span>
                            </v-tooltip>
                        </v-tab>
                    </v-tabs>
                </v-row>

                <div v-show="selectedSubTab === 0">
                    <new-packing :loading="executing" @execute="execute" @execute-multi-layer="executeMultiLayer"/>
                </div>

                <div v-show="selectedSubTab === 1">
                    <assign-properties @assignProperties="assignProperties"/>
                </div>

                <div v-show="selectedSubTab === 2">
                    <download-packing ref="refExportPacking"/>
                </div>

                <div v-show="selectedSubTab === 3">
                    <import-packing ref="refImportPacking" @loadtxtpacking="loadTxtPacking" @loadJSON="loadedJSON"/>
                </div>

                <div v-show="selectedSubTab === 4">
                    <download-image-form @download="downloadImage"/>
                </div>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
    import PropertiesTab from "./Properties.vue";
    import PolygonsTab from "./Polygons.vue";
    import api from "../../services/api.services";
    import Swatches from 'vue-swatches';
    import * as poly2tri from 'poly2tri';
    import validation from './../../services/validation.service';

    import "vue-swatches/dist/vue-swatches.min.css"
    import BoundaryConditions from "../templates/BoundaryConditions.vue";
    import AssignProperties from "../templates/AssignProperties.vue";
    import ImportPacking from "../templates/forms/ImportPacking.vue";
    import DownloadPacking from "../templates/forms/DownloadPacking.vue";
    import NewPacking from "../templates/NewPacking.vue";
    import UploadResultsDialog from "../templates/UploadResultsDialog.vue";
    import Polygon from "../geometry/konvas/polygon";
    import DragBox from "../geometry/konvas/dragBox";
    import DownloadImageForm from "../templates/forms/DownloadImageForm.vue";

    import { mdiShapePlus, mdiPlusCircle, mdiPalette, mdiClipboardText, mdiDownload, mdiUpload, mdiFileImage} from '@mdi/js';

    export default {
        components: {
            DownloadImageForm,
            UploadResultsDialog,
            NewPacking,
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
                selectedTab: 0,
                selectedSubTab: 0,
                stage: null,
                layer: null,
                dragBox: null,
                polygonsShape: [],
                dialogBoundaryConditions: false,
                timeout: 1200,
                dialog: false,
                dialog2: false,
                dialogAngle: false,
                categories: [
                    {
                        tab: 0,
                        name: "Packing",
                        icon: mdiShapePlus,
                        subTabs: [0, 1],
                        selectedSubTab: 0,
                        subcategories: [
                            {
                                name: 'Create New Packing',
                                icon: mdiPlusCircle,
                                tab: 0
                            },
                            {
                                name: 'Assign Properties',
                                icon: mdiPalette,
                                tab: 1
                            },
                        ]
                    },
                    {
                        tab: 1,
                        name: "File",
                        icon: mdiClipboardText,
                        subTabs: [2, 3, 4],
                        selectedSubTab: 2,
                        subcategories: [
                            {
                                name: "Download Mesh File",
                                icon: mdiDownload,
                                tab: 2
                            },
                            {
                                name: "Upload Mesh File",
                                icon: mdiUpload,
                                tab: 3
                            },
                            {
                                name: "Download Image",
                                icon: mdiFileImage,
                                tab: 4
                            },
                        ]
                    },
                ],
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

            setTimeout(() => {
                window.dispatchEvent(new Event('resize'));
            }, 310)
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
            if (!this.stage) {
                this.stage = new Konva.Stage({
                    container: 'myContainer',
                    width: 100,
                    height: 100
                });

                this.layer = new Konva.Layer();
                this.stage.add(this.layer);

                this.loadMesh();

                // adapt the stage on any window resize
                window.addEventListener('resize', () => {
                    let container = this.$refs.polygonContainer;
                    if (this.$refs.polygonContainer) {
                        this.stage.height(container.clientHeight);
                        this.stage.width(container.clientWidth);
                        this.layer.batchDraw();
                    }
                });
            }
        },
        methods: {
            createPackingPolygons() {
                if (this.packing && this.packing.polygons) {

                    this.layer.destroyChildren();

                    this.polygonsShape = [];
                    this.packing.polygons.forEach(pol => {
                        this.polygonsShape.push(new Polygon(pol, this.packing.width, this.packing.height, this.stage, this.layer, this.properties));
                    });

                    this.dragBox = new DragBox(this.polygonsShape, this.packing.width, this.packing.height, this.stage, this.layer);
                }
            },
            selectTab(i, tab) {
                if (tab) {
                    this.selectedSubTab = tab.selectedSubTab;
                }
                this.selectedTab = i;

            },
            selectSubTab(i, tab) {
                if (tab) {
                    tab.selectedSubTab = i;
                }
                this.selectedSubTab = i;
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
            loadOriginal() {
                this.executing = true;
                let packing = JSON.parse(JSON.stringify(this.packing.originalPacking));
                this.$store.commit('newPacking', packing);
                this.triangulateMesh();
                this.createPackingPolygons();
                this.findBoundaryElements();
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
                            if( this.packing.polygons[polygon.getIndex() - 1]) {
                                this.packing.polygons[polygon.getIndex() - 1].properties = assignProperties.filter(prop => Object.keys(this.properties).includes(prop.key));
                            }
                        }
                    }
                });
                this.$store.commit("assignProperties", this.packing);
                this.layer.batchDraw();

                this.openedDialog = false;
                this.dialog2 = false;
            },
            updatePacking(resp) {
                this.executing = false;
                let packing = this.triangulatePacking(resp.body.mesh);
                this.$store.commit("newPacking", packing);
                this.findBoundaryElements();
                this.createPackingPolygons();
                this.layer.batchDraw();
                this.openedDialog = false;
            },
            findBoundaryElements() {
                let segmentsCount = {};
                this.packing.polygons.forEach((pol, index) => {
                    for (let i = 0; i < pol.points.length; i++) {
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
                            if (!([x1, y1] in segmentsCount)) {
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
                        if (segmentsCount[k][k2].count === 1) {
                            borderSegments.push({
                                pntA: segmentsCount[k][k2].pntA,
                                pntB: segmentsCount[k][k2].pntB,
                                polIndex: segmentsCount[k][k2].polIndex,
                            });

                            if (!borderPoints.includes(segmentsCount[k][k2].pntA)) borderPoints.push(segmentsCount[k][k2].pntA);
                            if (!borderPoints.includes(segmentsCount[k][k2].pntB)) borderPoints.push(segmentsCount[k][k2].pntB);
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
                this.createPackingPolygons();
                this.layer.batchDraw();
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
            downloadImage(pixelRatio) {
                let filename = 'results.png';
                let dataURL = this.stage.toCanvas({pixelRatio: pixelRatio});
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
            },
        },
    }
</script>

<style scoped>

    .polygon {
        padding: 0;
        margin: 0;
        height: 100%;
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