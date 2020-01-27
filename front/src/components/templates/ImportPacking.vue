<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">Import Packing</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="fileImportForm">
                    <v-row justify="center">
                        <v-checkbox color="primary" v-model="parameters[0]" class="mx-2" label="Vertices"></v-checkbox>
                        <v-checkbox color="primary" v-model="parameters[1]" class="mx-2" label="Edges"></v-checkbox>
                        <v-checkbox color="primary" v-model="parameters[2]" class="mx-2" label="Properties"></v-checkbox>
                        <v-checkbox color="primary" v-model="parameters[3]" class="mx-2" label="Polygons"></v-checkbox>
                    </v-row>
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-file-input v-model="file" accept=".txt,.json" prepend-icon="mdi-file-code"
                                          :rules="[validation.required()]" show-size
                                          label="Upload mesh file"></v-file-input>
                        </v-col>
                    </v-row>
                </v-form>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                <v-btn dark color="teal lighten-2" @keyup.enter="loadPacking" @click.native="loadPacking">Load</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import validation from './../../services/validation.service';
    import * as poly2tri from 'poly2tri';

    export default {
        name: "ImportPacking",
        data() {
            return {
                dialog: false,
                parameters: [true, true, true, true],
                validation: validation,
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
                if (this.$refs.fileImportForm.validate()) {
                    if (this.file.type === 'application/json') {
                        this.file.text().then(res => {
                            this.$store.commit("loadPacking", JSON.parse(res));
                            this.close();
                        });
                    } else if (this.file.type === 'text/plain') {
                        this.file.text().then(res => {
                            try {
                                let i = 0;
                                let lines = res.split("\n");
                                let firstLine = lines[i].split(" ");
                                let numberOfPoints = 0;
                                let numberOfEdges = 0;
                                let numberOfProperties = 0;
                                let numberOfPolygons = 0;
                                if(this.parameters[0]) {
                                    numberOfPoints = parseInt(firstLine[i]);
                                    i+= 1;
                                }

                                if(this.parameters[1]) {
                                    numberOfEdges = parseInt(firstLine[i]);
                                    i+= 1;
                                }

                                if(this.parameters[2]) {
                                    numberOfProperties = parseInt(firstLine[i]);
                                    i+= 1;
                                }

                                if(this.parameters[3]) {
                                    numberOfPolygons = parseInt(firstLine[i]);
                                    i+= 1;
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

                                            if(minX > x) minX = x;
                                            if(minY > y) minY = y;
                                            points[index] = {
                                                x: x,
                                                y: y,
                                                index: index,
                                            }
                                        } else if (index <= numberOfPoints + numberOfEdges) {

                                        } else if (index <= numberOfPoints + numberOfEdges + numberOfProperties) {
                                            if (line in this.properties) {
                                                propertiesFile[index - (numberOfPoints + numberOfEdges)] = this.properties[line];
                                            } else {
                                                this.$store.commit("addProperty", {
                                                    label: lines[i],
                                                    typeOfValue: "Number",
                                                    color: "#F44336",
                                                    default: "11",
                                                    selected: false
                                                });
                                                propertiesFile[index - (numberOfPoints + numberOfEdges)] = this.properties[line];
                                            }

                                        } else if (index <= numberOfPoints + numberOfEdges + numberOfProperties + numberOfPolygons) {
                                            let polygonLine = line.split(" ");
                                            let vertices = parseInt(polygonLine[0]);
                                            let pointsArray = [];
                                            let propertiesArray = [];
                                            let numberOfProperties2 = ((vertices + 3) < polygonLine.length)?  parseInt(polygonLine[vertices + 3]): 0;

                                            Array(vertices).fill(undefined).map((_, i) => {
                                                pointsArray.push(points[polygonLine[i + 1]]);
                                            });

                                            Array(numberOfProperties2).fill(undefined).map((_, i) => {
                                                let aProperty = propertiesFile[polygonLine[i + vertices + 4]];
                                                propertiesArray.push({key: aProperty.label, value: aProperty.default});
                                            });


                                            polygons.push({
                                                label: "",
                                                radius: null,
                                                points: pointsArray,
                                                area: ((vertices + 1) < polygonLine.length)? parseFloat(polygonLine[vertices + 1]): 0,
                                                hole: ((vertices + 1) < polygonLine.length)? parseInt(polygonLine[vertices + 2]) === 1: true,
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
                                        if(!pnt.visited) {
                                            pnt.x -= minX;
                                            pnt.y -= minY;
                                            pnt.visited = true;
                                            if(pnt.x > maxX) maxX = pnt.x;
                                            if(pnt.y > maxY) maxY = pnt.y;
                                        }
                                    });
                                    let triangulation = [];
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
                                        triangulation.push(triangle);
                                    });
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
                            } catch (e) {
                                console.log(e);
                                this.$toast("Cannot load file (wrong format)");
                            }
                        });
                    } else {
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