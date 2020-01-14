<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">Import Packing</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="fileImportForm">
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-file-input v-model="file" accept=".txt,.json" prepend-icon="mdi-file-code" :rules="[validation.required()]" show-size label="Upload mesh file"></v-file-input>
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
                validation: validation,
                file: null
            }
        },
        computed: {
            properties () {
                return this.$store.getters.getProperties;
            },
        },
        created() {
            this.validation.changeLanguage('en');
        },
        methods: {
            loadPacking() {
                if (this.$refs.fileImportForm.validate()) {
                    if(this.file.type === 'application/json') {
                        this.file.text().then(res => {
                            this.$store.commit("loadPacking", JSON.parse(res));
                            this.close();
                        });
                    } else if(this.file.type === 'text/plain') {
                        this.file.text().then(res => {
                            try {
                                let lines = res.split("\n");
                                let firstLine = lines[0].split(" ");
                                let numberOfPoints = parseInt(firstLine[0]);
                                let numberOfEdges = parseInt(firstLine[1]);
                                let numberOfProperties = parseInt(firstLine[2]);
                                let numberOfPolygons = parseInt(firstLine[3]);
                                let points = {};
                                let propertiesFile = {};
                                let polygons = [];
                                let height = 0;
                                let width = 0;
                                lines.forEach((line, index) => {
                                    if(index > 0) {
                                        if (index <= numberOfPoints) {
                                            let pnt = line.split(" ");
                                            let x = parseFloat(pnt[0]);
                                            let y = parseFloat(pnt[1]);
                                            width = (width < x) ? x : width;
                                            height = (height < y) ? y : height;
                                            points[index] = {
                                                x: x,
                                                y: y
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
                                            let numberOfProperties = parseInt(polygonLine[vertices + 3]);

                                            Array(vertices).fill(undefined).map((_, i) => {
                                                pointsArray.push(points[polygonLine[i + 1]]);
                                            });

                                            Array(numberOfProperties).fill(undefined).map((_, i) => {
                                                let aProperty = propertiesFile[polygonLine[i + vertices + 4]];
                                                propertiesArray.push({key: aProperty.label, value: aProperty.default});
                                            });

                                            let triangulation = [];
                                            if (propertiesArray.length > 0) {
                                                let contour = [];
                                                pointsArray.forEach(pnt => {
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
                                            }

                                            polygons.push({
                                                label: "",
                                                radius: null,
                                                points: pointsArray,
                                                area: parseFloat(polygonLine[vertices + 1]),
                                                hole: parseInt(polygonLine[vertices + 2]) === 1,
                                                properties: propertiesArray,
                                                triangulation: triangulation
                                            });
                                        }
                                    }
                                });

                                this.close();
                                this.$emit("loadtxtpacking", {
                                    polygons: polygons,
                                    height: height,
                                    width: width
                                });
                            } catch (e) {
                                console.log(e);
                                alert("Cannot load file (wrong format)");
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
                this.$refs.fileImportForm.resetValidation();
            },
        }
    }
</script>

<style scoped>

</style>