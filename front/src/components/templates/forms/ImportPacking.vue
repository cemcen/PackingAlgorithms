<template>
    <v-card elevation="0">

        <v-card-text>
            <v-row justify="center" class="pt-4 pb-5">
                <v-btn dark color="teal lighten-2" :loading="isLoading" @click.native="loadPacking">
                    Load
                </v-btn>
            </v-row>
            <v-row justify="center">
                <v-col class="pa-0 pr-3 pl-3">
                    <v-select label="Choose type of file you want to import"
                              item-text="name"
                              v-model="selectedFileType" :items="fileTypes"
                              return-object>
                    </v-select>
                </v-col>
            </v-row>
            <v-form ref="triangulationFileImportForm" v-show="selectedFileType.value === 1">
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-file-input v-model="file" accept=".txt,.json" prepend-icon="mdi-file-code"
                                      :rules="[validation.required()]" show-size
                                      label="Upload mesh file"></v-file-input>
                    </v-col>
                </v-row>
            </v-form>
            <v-form ref="fileImportForm" v-show="selectedFileType.value === 0">
                <v-row>
                    <v-col sm="12" class="pa-0 ma-0">
                        <v-checkbox color="primary" v-model="parameters[0]" class="mx-2 mt-0" label="Vertices"></v-checkbox>
                    </v-col>
                    <v-col sm="12" class="pa-0 ma-0">
                        <v-checkbox color="primary" v-model="parameters[1]" class="mx-2 mt-0" label="Edges"></v-checkbox>
                    </v-col>
                    <v-col sm="12" class="pa-0 ma-0">
                        <v-checkbox color="primary" v-model="parameters[2]" class="mx-2 mt-0" label="Properties"></v-checkbox>
                    </v-col>
                    <v-col sm="12" class="pa-0 ma-0">
                        <v-checkbox color="primary" v-model="parameters[3]" class="mx-2 mt-0" label="Polygons"></v-checkbox>
                    </v-col>
                </v-row>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-file-input v-model="file" accept=".txt,.json"
                                      :rules="[validation.required()]" show-size
                                      label="Upload mesh file"></v-file-input>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from '../../../services/validation.service';
    import Delaunator from 'delaunator';
    import * as poly2tri from "poly2tri";

    String.prototype.hashCode = function () {
        let hash = 0, i, chr;
        if (this.length === 0) return hash;
        for (i = 0; i < this.length; i++) {
            chr = this.charCodeAt(i);
            hash = ((hash << 5) - hash) + chr;
            hash |= 0; // Convert to 32bit integer
        }
        return hash;
    };

    export default {
        name: "ImportPacking",
        data() {
            return {
                dialog: false,
                parameters: [true, true, true, true],
                validation: validation,
                selectedFileType: {value: 0, name: 'Packing File'},
                isLoading: false,
                fileTypes: [
                    {value: 0, name: 'Packing File'},
                    {value: 1, name: 'Triangulation File'}
                ],
                file: null
            }
        },
        computed: {
            properties() {
                return this.$store.getters.getProperties;
            },
        },
        created() {
            this.validation.changeLanguage('en');
        },
        methods: {
            loadPacking() {

                function calculateAllCrossProduct(points) {
                    let lastSign = null;

                    for (let i = 2; i < points.length; i++) {
                        //calculate crossproduct from 3 consecutive points
                        const crossproduct = calculateCrossProduct(points[i - 2], points[i - 1], points[i]);
                        const currentSign = Math.sign(crossproduct);
                        if (lastSign == null) {
                            //last sign init
                            lastSign = currentSign;
                        }

                        const checkResult = checkCrossProductSign(lastSign, currentSign);
                        if (checkResult === false) {
                            //different sign in cross products,no need to check the remaining points --> concave polygon --> return function
                            return false;
                        }
                        lastSign = currentSign;
                    }

                    //first point must check between second and last point, this is the last 3 points that can break convexity
                    const crossproductFirstPoint = calculateCrossProduct(points[points.length - 2], points[0], points[1]);

                    return checkCrossProductSign(lastSign, Math.sign(crossproductFirstPoint));
                }

                function checkCrossProductSign(lastSign, newSign) {
                    return lastSign === newSign;

                }

                function calculateCrossProduct(p1, p2, p3) {

                    let dx1 = p2.x - p1.x;
                    let dy1 = p2.y - p1.y;
                    let dx2 = p3.x - p2.x;
                    let dy2 = p3.y - p2.y;

                    return dx1 * dy2 - dy1 * dx2;
                }

                function isPolygonConvex(points) {
                    return calculateAllCrossProduct(points);
                }

                if ((this.selectedFileType.value === 0 && this.$refs.fileImportForm.validate()) || (this.selectedFileType.value === 1 && this.$refs.triangulationFileImportForm.validate())) {
                    this.isLoading = true;
                    if (this.file.type === 'application/json') {
                        if (this.selectedFileType.value === 0) {
                            this.file.text().then(res => {
                                this.$store.commit("loadPacking", JSON.parse(res));
                                this.$emit("loadJSON");
                                this.close();
                                this.isLoading = false;
                            });
                        }
                    } else if (this.file.type === 'text/plain') {
                        this.file.text().then(res => {
                            if (this.selectedFileType.value === 0) {
                                try {
                                    let i = 0;
                                    let lines = res.split("\n");
                                    let firstLine = lines[i].split(" ");
                                    let numberOfPoints = 0;
                                    let numberOfEdges = 0;
                                    let numberOfProperties = 0;
                                    let numberOfPolygons = 0;
                                    let numberOfHoles = 0;
                                    if (this.parameters[0]) {
                                        numberOfPoints = parseInt(firstLine[i]);
                                        i += 1;
                                    }

                                    if (this.parameters[1]) {
                                        numberOfEdges = parseInt(firstLine[i]);
                                        i += 1;
                                    }

                                    if (this.parameters[2]) {
                                        numberOfProperties = parseInt(firstLine[i]);
                                        i += 1;
                                    }

                                    if (this.parameters[3]) {

                                        numberOfPolygons = parseInt(firstLine[i]);
                                        i += 1;
                                        if (firstLine.length > i) {
                                            numberOfHoles = parseInt(firstLine[i]);
                                            i += 1;
                                        }
                                    }

                                    let points = {};
                                    let propertiesFile = {};
                                    let polygons = [];
                                    let minX = Number.MAX_VALUE;
                                    let minY = Number.MAX_VALUE;
                                    lines.forEach((line, index) => {
                                        if (index > 0) {
                                            if (index <= numberOfPoints) {
                                                let pnt = line.split(" ");
                                                let x = parseFloat(pnt[0]);
                                                let y = parseFloat(pnt[1]);

                                                if (minX > x) minX = x;
                                                if (minY > y) minY = y;
                                                points[index] = {
                                                    x: x,
                                                    y: y,
                                                    index: index,
                                                }
                                            } else if (index <= numberOfPoints + numberOfEdges) {

                                            } else if (index <= numberOfPoints + numberOfEdges + numberOfProperties) {
                                                if (line in this.properties) {
                                                    propertiesFile[index - (numberOfPoints + numberOfEdges)] = this.properties[line.hashCode()];
                                                } else {
                                                    this.$store.commit("addProperty", {
                                                        label: line.hashCode(),
                                                        typeOfValue: "Number",
                                                        color: "#F44336",
                                                        default: line,
                                                        selected: false
                                                    });
                                                    propertiesFile[index - (numberOfPoints + numberOfEdges)] = this.properties[line.hashCode()];
                                                }

                                            } else if (index <= numberOfPoints + numberOfEdges + numberOfProperties + numberOfPolygons) {
                                                let polygonLine = line.split(" ");
                                                let vertices = parseInt(polygonLine[0]);
                                                let pointsArray = [];
                                                let propertiesArray = [];
                                                let numberOfProperties2 = ((vertices + 3) < polygonLine.length) ? parseInt(polygonLine[vertices + 3]) : 0;

                                                Array(vertices).fill(undefined).map((_, i) => {
                                                    pointsArray.push(points[polygonLine[i + 1]]);
                                                });

                                                Array(numberOfProperties2).fill(undefined).map((_, i) => {
                                                    let aProperty = propertiesFile[polygonLine[i + vertices + 4]];
                                                    propertiesArray.push({
                                                        key: aProperty.label,
                                                        value: aProperty.default
                                                    });
                                                });


                                                polygons.push({
                                                    label: "",
                                                    radius: null,
                                                    points: pointsArray,
                                                    indexPol: index - (numberOfPoints + numberOfEdges + numberOfProperties),
                                                    area: ((vertices + 1) < polygonLine.length) ? parseFloat(polygonLine[vertices + 1]) : 0,
                                                    hole: false,
                                                    properties: propertiesArray,
                                                    triangulation: []
                                                });

                                            } else if (index <= numberOfPoints + numberOfEdges + numberOfProperties + numberOfPolygons + numberOfHoles) {
                                                let polygonLine = line.split(" ");
                                                let vertices = parseInt(polygonLine[0]);
                                                let pointsArray = [];
                                                let propertiesArray = [];
                                                let numberOfProperties2 = ((vertices + 3) < polygonLine.length) ? parseInt(polygonLine[vertices + 3]) : 0;

                                                Array(vertices).fill(undefined).map((_, i) => {
                                                    pointsArray.push(points[polygonLine[i + 1]]);
                                                });

                                                Array(numberOfProperties2).fill(undefined).map((_, i) => {
                                                    let aProperty = propertiesFile[polygonLine[i + vertices + 4]];
                                                    propertiesArray.push({
                                                        key: aProperty.label,
                                                        value: aProperty.default
                                                    });
                                                });


                                                polygons.push({
                                                    label: "",
                                                    radius: null,
                                                    points: pointsArray,
                                                    indexPol: index - (numberOfPoints + numberOfEdges + numberOfProperties),
                                                    area: ((vertices + 1) < polygonLine.length) ? parseFloat(polygonLine[vertices + 1]) : 0,
                                                    hole: true,
                                                    properties: propertiesArray,
                                                    triangulation: []
                                                });
                                            }
                                        }
                                    });


                                    let maxX = 0;
                                    let maxY = 0;
                                    polygons.forEach(pol => {
                                        pol.points = pol.points.filter(function (el) {
                                            return el != null;
                                        });
                                        // console.log(pol.points);
                                        pol.points.forEach(pnt => {
                                            if (!pnt.visited) {
                                                pnt.oldX = pnt.x;
                                                pnt.oldY = pnt.y;
                                                pnt.x -= minX;
                                                pnt.y -= minY;
                                                pnt.visited = true;
                                                if (pnt.x > maxX) maxX = pnt.x;
                                                if (pnt.y > maxY) maxY = pnt.y;
                                            }
                                        });

                                        const isConvex = isPolygonConvex(pol.points);

                                        if(!isConvex) {
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
                                        } else {
                                            let triangulation = [];
                                            let contour = [];
                                            pol.points.forEach(pnt => {
                                                contour.push([pnt.x, pnt.y])
                                            });
                                            const delaunay = Delaunator.from(contour);
                                            for (let i = 0; i < delaunay.triangles.length; i += 3) {
                                                let pointsT = [contour[delaunay.triangles[i]], contour[delaunay.triangles[i + 1]], contour[delaunay.triangles[i + 2]]];
                                                let triangle = [];
                                                pointsT.forEach(pnt => {
                                                    triangle.push({x: pnt[0], y: pnt[1]});
                                                });
                                                triangulation.push(triangle);
                                            }
                                            pol.triangulation = triangulation;
                                        }
                                    });

                                    let height = maxY;
                                    let width = maxX;

                                    this.close();
                                    this.$emit("loadtxtpacking", {
                                        polygons: polygons,
                                        height: height,
                                        width: width
                                    });
                                    this.isLoading = false;
                                } catch (e) {
                                    console.log(e);
                                    this.isLoading = false;
                                    this.$toast("Cannot load file (wrong format)");
                                }
                            } else if (this.selectedFileType.value === 1) {
                                try {

                                    let i = 0;
                                    let lines = res.split("\n");
                                    let firstLine = lines[i].split(" ");
                                    let numberOfPoints = 0;
                                    let numberOfTriangles = 0;
                                    numberOfPoints = parseInt(firstLine[i]);
                                    i += 1;
                                    numberOfTriangles = parseInt(firstLine[i]);

                                    let points = {};
                                    let propertiesFile = {};
                                    let polygons = [];
                                    let minX = Number.MAX_VALUE;
                                    let minY = Number.MAX_VALUE;
                                    lines.forEach((line, index) => {
                                        if (index > 0) {
                                            if (index <= numberOfPoints) {
                                                let pnt = line.split(" ");
                                                let x = parseFloat(pnt[0]);
                                                let y = parseFloat(pnt[1]);

                                                if (minX > x) minX = x;
                                                if (minY > y) minY = y;
                                                points[index] = {
                                                    x: x,
                                                    y: y,
                                                    index: index,
                                                }
                                            } else if (index <= numberOfPoints) {

                                            } else if (index <= numberOfPoints + numberOfTriangles) {
                                                let polygonLine = line.split(" ");
                                                let vertices = 3;
                                                let pointsArray = [];
                                                let propertiesArray = [];
                                                let numberOfProperties2 = ((vertices + 3) < polygonLine.length) ? parseInt(polygonLine[vertices + 3]) : 0;

                                                Array(vertices).fill(undefined).map((_, i) => {
                                                    pointsArray.push(points[polygonLine[i]]);
                                                });

                                                Array(numberOfProperties2).fill(undefined).map((_, i) => {
                                                    let aProperty = propertiesFile[polygonLine[i + vertices + 4]];
                                                    propertiesArray.push({
                                                        key: aProperty.label,
                                                        value: aProperty.default
                                                    });
                                                });


                                                polygons.push({
                                                    label: "",
                                                    radius: null,
                                                    points: pointsArray,
                                                    indexPol: index - (numberOfPoints + numberOfTriangles),
                                                    area: 0,
                                                    hole: true,
                                                    properties: propertiesArray,
                                                    triangulation: []
                                                });
                                            }
                                        }
                                    });


                                    let maxX = 0;
                                    let maxY = 0;
                                    polygons.forEach(pol => {
                                        pol.points.forEach(pnt => {
                                            if (!pnt.visited) {
                                                pnt.oldX = pnt.x;
                                                pnt.oldY = pnt.y;
                                                pnt.x -= minX;
                                                pnt.y -= minY;
                                                pnt.visited = true;
                                                if (pnt.x > maxX) maxX = pnt.x;
                                                if (pnt.y > maxY) maxY = pnt.y;
                                            }
                                        });
                                        let triangulation = [];
                                        let contour = [];
                                        pol.points.forEach(pnt => {
                                            contour.push([pnt.x, pnt.y])
                                        });
                                        const delaunay = Delaunator.from(contour);
                                        for (let i = 0; i < delaunay.triangles.length; i += 3) {
                                            let pointsT = [contour[delaunay.triangles[i]], contour[delaunay.triangles[i + 1]], contour[delaunay.triangles[i + 2]]];
                                            let triangle = [];
                                            pointsT.forEach(pnt => {
                                                triangle.push({x: pnt[0], y: pnt[1]});
                                            });
                                            triangulation.push(triangle);
                                        }
                                        pol.triangulation = triangulation;
                                    });

                                    let height = maxY;
                                    let width = maxX;

                                    this.close();
                                    this.$emit("loadtxtpacking", {
                                        polygons: polygons,
                                        height: height,
                                        width: width
                                    });
                                    this.isLoading = false;
                                } catch (e) {
                                    console.log(e);
                                    this.isLoading = false;
                                    this.$toast("Cannot load file (wrong format)");
                                }

                            }
                        });
                    } else {
                        this.isLoading = false;
                        this.$toast("File not supported");
                    }
                }
            },
            openDialog() {
                this.resetValidation();
                this.file = null;
                this.dialog = true;
            },
            close() {
                this.resetValidation();
                this.$emit("closedDialog");
                this.dialog = false;
            },
            resetValidation() {
                this.parameters = [true, true, true, true];
                this.$refs.fileImportForm.resetValidation();
            },
        }
    }
</script>

<style scoped>

</style>