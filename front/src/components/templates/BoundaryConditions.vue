<template>
    <v-dialog eager v-model="dialog" persistent style="height: 93vh !important;">
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
        <v-card>
            <v-row no-gutters>
                <v-col md="6">
                    <v-flex class="d-flex justify-center pl-3">
                        <span class="hint-style">Multiples segments and nodes on the boundary can be selected by holding down the OPTION/ALT button.</span>
                    </v-flex>

                    <div id='myContainerBC' ref="pCont" class="polygon">
                        <div id="polygonDrawerBorderConditions" ref="polygonDrawer"></div>
                    </div>

                </v-col>
                <v-col md="6">
                    <boundary-properties @assign-properties="assignProperties"/>
                </v-col>
            </v-row>
        </v-card>
        <add-border-points ref="addBorderPointRef" @changedBoundary="changedBoundary" :bs="borderSegments"/>
    </v-dialog>
</template>

<script>

    import Segment from "../geometry/konvas/segment";
    import BoundaryProperties from "./BoundaryProperties.vue";
    import AddBorderPoints from "./AddBorderPointsDialog.vue";
    import Polygon from "../geometry/konvas/polygon";
    import DragBox from "../geometry/konvas/dragBox";
    import Point from "../geometry/konvas/point";

    export default {
        name: "BorderConditions",
        components: {AddBorderPoints, BoundaryProperties},
        props: {
            dialog: {
                type: Boolean,
                defaultValue: false,
            },
            polygonShape: {
                type: Array,
                defaultValue: [],
            }
        },
        data() {
            return {
                borderPoints: [],
                borderSegments: [],
                borderElements: [],
                stage: null,
                layer: null,
                dragBox: null,
                polygonsShape: [],
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

            this.stage = new Konva.Stage({
                container: 'myContainerBC',
                width: 100,
                height: 100
            });

            this.loadMesh();

            // adapt the stage on any window resize
            window.addEventListener('resize', () => {
                let container = this.$refs.pCont;

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

                    this.dragBox = new DragBox(this.borderElements, this.packing.width, this.packing.height, this.stage, this.layer);
                    this.stage.add(this.layer);
                }
            },
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
                setTimeout(function () {
                    window.dispatchEvent(new Event('resize'));
                }, 310);
            },
            updatePacking() {
                this.loadMesh();
            },
            loadMesh() {
                this.createPackingPolygons();
                this.loadBorderElements();
            },
            loadBorderElements() {
                this.packing.borderElements.segments.forEach(be => {
                    this.borderSegments.push(new Segment(be.pntA.x, be.pntA.y, be.pntB.x, be.pntB.y,
                        this.packing.width, this.packing.height, this.layer, this.stage, this.properties, be.indexPol));
                });
                this.packing.borderElements.points.forEach(be => {
                    this.borderPoints.push(new Point(be.x, be.y,
                        this.packing.width, this.packing.height, this.layer, this.stage, this.properties));
                });

                this.borderElements = [...this.borderSegments, ...this.borderPoints];

                this.dragBox.setElements(this.borderElements);
            },
            assignProperties(selectedOptionProperties, selectedOptionType) {
                let sOP = selectedOptionProperties.value;
                let sOT = selectedOptionType.value;
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

    .polygon {
        padding: 0;
        margin: 0;
        height: 75vh;
        min-width: 100%;
    }

    .hint-style {
        font-size: smaller;
        color: #666;
    }
</style>