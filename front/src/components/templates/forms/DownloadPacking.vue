<template>
    <v-card elevation="0">
        <v-card-title>
            <v-row justify="center">
                <v-btn dark color="teal lighten-2" @keyup.enter="downloadPacking" @click.native="downloadPacking">
                    Download
                </v-btn>
            </v-row>
        </v-card-title>

        <v-card-text>
            <v-form ref="fileDownloadForm">
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-select v-model="selectedFileOption" :items="fileOptions" item-text="name" label="File Type"
                                  :rules="[validation.required()]" return-object></v-select>
                    </v-col>
                </v-row>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-text-field v-model="filename" label="Filename"
                                      :rules="[validation.required()]"></v-text-field>
                    </v-col>
                </v-row>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-select v-model="selectedOption" :items="fileDownloadOptions"
                                  item-text="type" label="File Format"
                                  :rules="[validation.required()]" return-object></v-select>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from '../../../services/validation.service';

    export default {
        name: "DownloadPacking",
        data() {
            return {
                validation: validation,
                file: null,
                filename: 'packing',
                fileDownloadOptions: [
                    {type: ".txt", value: 0},
                    {type: ".json", value: 1}
                ],
                selectedFileOption: {name: "Packing File", value: 0},
                fileOptions: [
                    {name: "Packing File", value: 0},
                    {name: "Triangulation File", value: 1}
                ],
                selectedOption: {type: ".txt", value: 0},
            }
        },
        created() {
            this.validation.changeLanguage('en');
        },
        computed: {
            packing() {
                return this.$store.getters.getPacking;
            },
            properties() {
                return this.$store.getters.getProperties;
            },
            polygons() {
                return this.$store.getters.getPolygons;
            },
        },
        methods: {
            downloadPacking() {
                if (this.$refs.fileDownloadForm.validate()) {
                    if (this.selectedFileOption.value === 0) {
                        if (this.selectedOption.value === 0) {
                            this.exportPackingTxt();
                        } else {
                            this.exportPackingJSON();
                        }
                    } else {
                        if (this.selectedOption.value === 0) {
                            this.exportTriangulationTxt();
                        } else {
                            this.exportTriangulationJSON();
                        }
                    }
                    this.close();
                }
            },
            openDialog() {
                this.resetValidation();
                this.filename = 'packing';
                this.selectedOption = {type: ".txt", value: 0};
            },
            close() {
                this.resetValidation();
                this.selectedFileOption = {name: "Packing File", value: 0};
                this.selectedOption = {type: ".txt", value: 0};
                this.filename = 'packing';
            },
            resetValidation() {
                this.$refs.fileDownloadForm.resetValidation();
            },
            exportTriangulationTxt() {
                let file = '';
                let points = this.packing.draw.points;
                let polygons = this.packing.draw.polygons;
                let triangles = this.packing.polygons.reduce((acc, ele) => acc + ele.triangulation.length, 0);

                let sortedPoints = this.sortDictionary(points);
                let sortedPolygons = this.sortPolygons(polygons);

                file += Object.keys(points).length
                    + ' ' + triangles
                    + ' ' + Object.keys(sortedPolygons.filter(pol => !pol[2].hole)).length
                    + ' ' + Object.keys(sortedPolygons.filter(pol => pol[2].hole)).length
                    + '\n';

                sortedPoints.forEach(point => {
                    let splitted = point[0].split(",");
                    file += (Math.abs(splitted[0]) < 1e-8 ? 0 : splitted[0]) + ' ' + (Math.abs(splitted[1]) < 1e-8 ? 0 : splitted[1]) + '\n';
                });

                let counterTriangle = 1;
                sortedPolygons.forEach(pol => {
                    pol[2].triangulation.forEach(triang => {


                        triang.forEach(pnt => {
                            pnt.triangle = counterTriangle;
                            file += points[[pnt.x, pnt.y]] + ' ';
                        });

                        counterTriangle += 1;
                        file = file.slice(0, -1);
                        file += '\n';
                    });
                });

                sortedPolygons.filter(pol => !pol[2].hole).forEach(pol => {
                    file += pol[1] + ' ';
                    file += pol[2].triangulation.length + ' ';
                    pol[2].triangulation.forEach(triang => {
                        file += triang[0].triangle + ' ';
                    });
                    file = file.slice(0, -1);
                    file += '\n';
                });

                sortedPolygons.filter(pol => pol[2].hole).forEach(pol => {
                    file += pol[1] + ' ';
                    file += pol[2].triangulation.length + ' ';
                    pol[2].triangulation.forEach(triang => {
                        file += triang[0].triangle + ' ';
                    });
                    file = file.slice(0, -1);
                    file += '\n';
                });

                this.downloadTxtFile(file);
            },
            exportTriangulationJSON() {
                let triangulation = [];

                this.packing.polygons.forEach(pol => {
                    triangulation.push(pol.triangulation);
                });
                let exportObj = {
                    triangulation: triangulation,
                };
                this.downloadJsonFile(exportObj);
            },
            exportPackingJSON: function () {
                let exportObj = {
                    packing: this.packing,
                    properties: this.properties,
                    polygons: this.polygons
                };
                this.downloadJsonFile(exportObj);
            },
            exportPackingTxt() {
                let file = '';
                let points = {};
                let edges = {};
                let polygons = {};
                let borderPoints = {};
                let borderSegments = {};
                let meshPolygons = this.packing.polygons;
                let meshBorderPoints = this.packing.borderElements.points;
                let meshBorderSegments = this.packing.borderElements.segments;

                let pointIndex = 1;
                let edgeIndex = 1;
                let polygonIndex = 1;

                meshPolygons.forEach(pol => {
                    let indexArray = [];
                    pol.points.forEach((pnt, index) => {
                        let pntA = pnt;
                        let pntB = pol.points[(index + 1) % pol.points.length];

                        let x1 = pntA.x;
                        let y1 = pntA.y;
                        let x2 = pntB.x;
                        let y2 = pntB.y;

                        if (!([x1, y1] in points)) {
                            points[[x1, y1]] = pointIndex;
                            pointIndex += 1;
                        }

                        if (!([x2, y2] in points)) {
                            points[[x2, y2]] = pointIndex;
                            pointIndex += 1;
                        }

                        if (!([points[[x1, y1]], points[[x2, y2]]] in edges) && !([points[[x2, y2]], points[[x1, y1]]] in edges)) {
                            edges[[points[[x1, y1]], points[[x2, y2]]]] = edgeIndex;
                            edgeIndex += 1;
                        }
                        indexArray.push(points[[x1, y1]]);
                    });

                    polygons[indexArray] = {
                        count: polygonIndex,
                        polygon: pol,
                    };

                    pol.indexExport = polygonIndex;

                    polygonIndex += 1;
                });

                let borderPointsIndex = 1;
                meshBorderPoints.forEach(mBP => {

                    borderPoints[[mBP.x, mBP.y]] = {
                        index: borderPointsIndex,
                        pointIndex: points[[mBP.x, mBP.y]],
                        properties: mBP.properties,
                        point: [mBP.x, mBP.y]
                    };

                    borderPointsIndex += 1;
                });

                let borderSegmentsIndex = 1;
                meshBorderSegments.forEach(mBS => {
                    let seg = [mBS.pntA.x, mBS.pntA.y, mBS.pntB.x, mBS.pntB.y];

                    borderSegments[[points[[seg[0], seg[1]]], points[[seg[2], seg[3]]]]] = {
                        index: borderSegmentsIndex,
                        segmentIndex: edges[[points[[seg[0], seg[1]]], points[[seg[2], seg[3]]]]],
                        polygon: meshPolygons[mBS.polIndex].indexExport,
                        properties: mBS.properties
                    };

                    borderSegmentsIndex += 1;
                });

                let sortedPoints = this.sortDictionary(points);
                let sortedEdges = this.sortDictionary(edges);
                let sortedPolygons = this.sortPolygons(polygons);
                let properties = this.properties;
                if (!properties) properties = {};
                let propertiesArray = Object.keys(properties);

                let borderPointsWithProperties = 0;
                Object.keys(borderPoints).forEach(bp => {
                    if (borderPoints[bp].properties && borderPoints[bp].properties.length > 0) borderPointsWithProperties += 1
                });

                let borderSegmentsWithProperties = 0;
                Object.keys(borderSegments).forEach(bs => {
                    if (borderSegments[bs].properties && borderSegments[bs].properties.length > 0) borderSegmentsWithProperties += 1
                });

                file += Object.keys(points).length
                    + ' ' + Object.keys(edges).length
                    + ' ' + propertiesArray.length
                    + ' ' + Object.keys(sortedPolygons.filter(pol => !pol[2].hole)).length
                    + ' ' + Object.keys(sortedPolygons.filter(pol => pol[2].hole)).length
                    + ' ' + borderPointsWithProperties
                    + ' ' + borderSegmentsWithProperties
                    + '\n';

                sortedPoints.forEach(point => {
                    let splitted = point[0].split(",");
                    file += (Math.abs(splitted[0]) < 1e-8 ? 0 : splitted[0]) + ' ' + (Math.abs(splitted[1]) < 1e-8 ? 0 : splitted[1]) + '\n';
                });

                sortedEdges.forEach(edge => {
                    let splitted = edge[0].split(",");
                    file += splitted[0] + ' ' + splitted[1] + '\n';
                });

                propertiesArray.forEach(prop => {
                    file += this.properties[prop].default + '\n';
                });

                sortedPolygons.filter(pol => !pol[2].hole).forEach(polygon => {
                    let splitted = polygon[0].split(",");
                    file += (splitted.length + ' ');
                    splitted.forEach(s => {
                        file += (s + ' ');
                    });
                    file += (polygon[2].area + ' ');

                    if (polygon[2].properties && polygon[2].properties.length > 0) {
                        file += (polygon[2].properties.length + ' ');
                        polygon[2].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    } else {
                        file += (0 + ' ');
                    }
                    file = file.slice(0, -1);
                    file += '\n';
                });

                sortedPolygons.filter(pol => pol[2].hole).forEach(polygon => {
                    let splitted = polygon[0].split(",");
                    file += (splitted.length + ' ');
                    splitted.forEach(s => {
                        file += (s + ' ');
                    });
                    file += (polygon[2].area + ' ');

                    if (polygon[2].properties && polygon[2].properties.length > 0) {
                        file += (polygon[2].properties.length + ' ');
                        polygon[2].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    } else {
                        file += (0 + ' ');
                    }
                    file = file.slice(0, -1);
                    file += '\n';
                });

                Object.keys(borderPoints).forEach(bp => {
                    if (borderPoints[bp].properties && borderPoints[bp].properties.length > 0) {
                        file += (borderPoints[bp].pointIndex + ' ');
                        if (borderPoints[bp].properties && borderPoints[bp].properties.length > 0) {
                            file += (borderPoints[bp].properties.length + ' ');
                            borderPoints[bp].properties.forEach(prop => {
                                file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                            })
                        } else {
                            file += (0 + ' ');
                        }
                        file = file.slice(0, -1);
                        file += '\n';
                    }
                });

                Object.keys(borderSegments).forEach(bs => {
                    if (borderSegments[bs].properties && borderSegments[bs].properties.length > 0) {
                        file += (borderSegments[bs].segmentIndex + ' ');

                        file += (borderSegments[bs].polygon + ' ');
                        file += ((sortedPolygons[borderSegments[bs].polygon - 1][0].split(",").indexOf(Object.keys(edges)[borderSegments[bs].segmentIndex - 1].split(",")[0]) + 1) + ' ');

                        if (borderSegments[bs].properties && borderSegments[bs].properties.length > 0) {
                            file += (borderSegments[bs].properties.length + ' ');
                            borderSegments[bs].properties.forEach(prop => {
                                file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                            })
                        } else {
                            file += (0 + ' ');
                        }
                        file = file.slice(0, -1);
                        file += '\n';
                    }
                });


                this.downloadTxtFile(file);
            },
            sortDictionary(dict) {
                // Create items array
                let sorted = Object.keys(dict).map(function (key) {
                    return [key, dict[key]];
                });

                // Sort the array based on the second element
                sorted.sort(function (first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
            sortPolygons(dict) {
                // Create items array
                let sorted = Object.keys(dict).map(function (key) {
                    return [key, dict[key].count, dict[key].polygon];
                });

                // Sort the array based on the second element
                sorted.sort(function (first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
            downloadTxtFile(file) {
                let filename = this.filename + '.txt';
                const blob = new Blob([file], {type: 'text/plain'});
                this.downloadFile(blob, filename, 'text/plain');
            },
            downloadJsonFile(json) {
                let filename = this.filename + '.json';
                const blob = new Blob([JSON.stringify(json)], {type: 'application/json'});
                this.downloadFile(blob, filename, 'application/json');
            },
            downloadFile(blob, filename, type) {
                const e = document.createEvent('MouseEvents'),
                    a = document.createElement('a');
                a.download = filename;
                a.href = window.URL.createObjectURL(blob);
                a.dataset.downloadurl = [type, a.download, a.href].join(':');
                e.initEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                a.dispatchEvent(e);
            }
        }
    }
</script>

<style scoped>

</style>