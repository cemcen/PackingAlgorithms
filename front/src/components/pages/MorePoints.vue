<template>
    <v-row style="height: 90vh">
        <v-col sm="8" class="pl-9 pt-0" style="height: 90vh">
            <v-card class="my-card">
                <!--<v-flex class="d-flex justify-center pl-3">
                    <span class="hint-style">Multiples segments and nodes on the boundary can be selected by holding down the COMMAND/CONTROL button.</span>
                </v-flex>-->

                <div id='myContainerBC' ref="pCont" class="polygon">
                    <div id="polygonDrawerBorderConditions" ref="polygonDrawer"></div>
                </div>
            </v-card>
        </v-col>

        <v-col sm="4" class="pt-0 pr-9">
            <v-card class="my-card">
                <v-card color="#ffffff" class="elevation-0">
                    <v-card-title>
                        <span class="headline">More Border Points</span>
                    </v-card-title>
                    <v-card-text>
                        <v-form ref="addBorderPointsForm">
                            <v-row justify="center">
                                <v-col class="pa-0 pr-3 pl-3">
                                    <v-select label="Choose which segments should be divided"
                                              item-text="name"
                                              v-model="selectedOptionType" :items="optionType"
                                              return-object>
                                    </v-select>
                                </v-col>
                            </v-row>
                            <v-row justify="center">
                                <v-col class="pa-0 pr-3 pl-3">
                                    <v-text-field v-model="divisionTimes" label="Division times" type="number"
                                                  :rules="[validation.required(), validation.greaterThanAndPositive(1)]"
                                                  clearable required>
                                    </v-text-field>
                                </v-col>
                            </v-row>
                        </v-form>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn dark color="teal lighten-2" @click.native="addPoints">
                            Add points
                        </v-btn>
                    </v-card-actions>
                </v-card>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
    import BoundaryProperties from "./../templates/BoundaryProperties.vue";
    import Polygon from "../geometry/konvas/polygon";
    import DragBox from "../geometry/konvas/dragBox";
    import Segment from "../geometry/konvas/segment";
    import Point from "../geometry/konvas/point";
    import validation from './../../services/validation.service';

    export default {
        name: "Boundary",
        components: {BoundaryProperties},
        data() {
            return {
                borderSegments: [],
                borderPoints: [],
                borderElements: [],
                divisionTimes: 2,
                validation: validation,
                stage: null,
                layer: null,
                dragBox: null,
                polygonsShape: [],
                selectedOptionType: {
                    value: 0,
                    name: "All Segments"
                },
                optionType: [
                    {
                        value: 0,
                        name: "All Segments"
                    },
                    {
                        value: 1,
                        name: "Only selected ones"
                    }
                ],
            }
        },
        created() {
            setTimeout(() => {
                window.dispatchEvent(new Event('resize'));
            }, 310)
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

                    this.polygonsShape = [];
                    this.packing.polygons.forEach(pol => {
                        this.polygonsShape.push(new Polygon(pol, this.packing.width, this.packing.height, this.stage, this.layer, this.properties));
                    });

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
                this.borderSegments = [];
                this.borderPoints = [];
                this.packing.borderElements.segments.forEach((be, index) => {
                    this.borderSegments.push(new Segment(be.pntA.x, be.pntA.y, be.pntB.x, be.pntB.y,
                        this.packing.width, this.packing.height, this.layer, this.stage, this.properties, be.polIndex, be, index));
                });

                this.packing.borderElements.points.forEach((be, index) => {
                    this.borderPoints.push(new Point(be.x, be.y,
                        this.packing.width, this.packing.height, this.layer, this.stage, this.properties, be, index));
                });

                this.borderElements = [...this.borderSegments, ...this.borderPoints];

                this.dragBox.setElements(this.borderElements);
            },
            assignProperties(selectedOptionProperties, selectedOptionType) {
                let sOP = selectedOptionProperties.value;
                let sOT = selectedOptionType.value;
                let borderSegments = this.packing.borderElements.segments;
                let borderPoints = this.packing.borderElements.points;
                this.borderSegments.forEach(seg => {
                    if (sOP === 0 || (seg.isSelected() && sOP === 1)) {
                        if (sOT === 0 || sOT === 2) {
                            let assignProperties = [];
                            let properties = this.properties;
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    assignProperties.push({key: item, value: properties[item].default})
                                }
                            });

                            seg.assignProperties(assignProperties, this.properties);
                            borderSegments[seg.getIndex()].properties = seg.getProperties();
                        }
                    }
                });
                this.$store.commit("borderElements", {segments: borderSegments, points: borderPoints});

                this.layer.batchDraw();
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
                let dataURL = this.stage.toCanvas({ pixelRatio: 5 });
                dataURL.toBlob((blob) => {
                    this.downloadFile(blob,filename, 'png');
                });
            },
            addPoints() {
                this.borderSegments.forEach(bsI => {
                    if ((bsI.isSelected() && this.selectedOptionType.value === 1) || this.selectedOptionType.value === 0) {
                        let polygon = this.packing.polygons[bsI.getPolygonIndex()];
                        let indices = [];
                        polygon.points.forEach((pnt, index) => {
                            if(bsI.containsWithoutTransform([pnt.x, pnt.y])) {
                                indices.push(index);
                            }
                        });

                        if(indices.length === 2) {
                            let pntA = indices[0];
                            let pntB = indices[1];
                            if(indices[0] > indices[1] || (indices[0] === 0 && indices[1] !== 1)) {
                                pntA = indices[1];
                                pntB = indices[0];
                            }

                            let x1 = polygon.points[pntA].x;
                            let y1 = polygon.points[pntA].y;
                            let x2 = polygon.points[pntB].x;
                            let y2 = polygon.points[pntB].y;

                            let index = pntB;

                            Array(this.divisionTimes - 1).fill().map((_, i) => {
                                let pnt = {
                                    x: x1 + ((i + 1)/this.divisionTimes)*(x2 - x1),
                                    y: y1 + ((i + 1)/this.divisionTimes)*(y2 - y1),
                                };


                                if(pntB === 0) {
                                    polygon.points.push(pnt);
                                }
                                else {
                                    polygon.points.splice(index, 0, pnt);
                                    index += 1;
                                }
                            });
                        }

                        this.packing.polygons[bsI.getPolygonIndex()] = Object.assign({}, polygon)
                    }
                });

                this.findBoundaryElements();
                this.loadMesh();

                this.stage.draw();
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
        height: 100%;
        min-width: 100%;
    }

    .my-card {
        height: 90vh;
    }

</style>