<template>
    <div class="page-container">
        <v-toolbar color="#eeeeee">
            <v-toolbar-title>Packing</v-toolbar-title>
            <v-divider class="mx-2" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <v-btn color="teal lighten-2" @click="exportPacking" class="mb-2">
                <v-icon>save_alt</v-icon>
                Export
            </v-btn>
            <v-btn color="teal lighten-2" @click="dialog = true" class="mb-2">Create New Packing</v-btn>
            <v-dialog v-model="dialog" max-width="500px">
                <v-card color="#ffffff">
                    <v-card-title>
                        <span class="headline">New Packing</span>
                    </v-card-title>

                    <v-card-text>
                        <v-layout justify-center>
                            <v-flex>
                                <v-text-field v-validate="'required|min_value:1'" :error-messages="errors.collect('width')"
                                              v-model="width" type="number" label="Container Width"
                                              data-vv-name="width" clearable required>
                                </v-text-field>
                                <v-text-field v-validate="'required|min_value:1'" :error-messages="errors.collect('height')"
                                              v-model="height" type="number" label="Container Height"
                                              data-vv-name="height" clearable required>
                                </v-text-field>
                                <v-checkbox color="black" v-model="randomShape" label="Random shape polygons?"
                                        required></v-checkbox>
                                <div v-if="!randomShape">
                                    <v-text-field v-validate="'required|min_value:1'" :error-messages="errors.collect('regularity')"
                                              v-model="regularity" type="number" label="Container Regularity" data-vv-name="regularity"
                                              clearable>
                                    </v-text-field>
                                </div>
                                <v-radio-group v-model="approachAlgorithm" row>
                                    <v-radio color="black" label="Border Preference" value="0"></v-radio>
                                    <v-radio color="black" label="Less Density" value="1"></v-radio>
                                </v-radio-group>
                            </v-flex>
                        </v-layout>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="teal lighten-2" @click.native="execute">Execute Algorithm</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>

            <v-dialog v-model="dialogInfo" max-width="500px">
                <v-card color="#ffffff" >
                    <v-card-title>
                        <span class="headline">Polygon Properties</span>
                    </v-card-title>

                    <v-card-text>
                        Number of vertex: {{this.polygon.points.length}}
                    </v-card-text>
                </v-card>
            </v-dialog>
        </v-toolbar>
        <div id='myContainer' @click="this.showPolygonData" ref="polygonContainer" class="polygon">
            <div ref="polygonDrawer"></div>
        </div>
    </div>
</template>

<script>
    import api from "../services/api.services";

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        name: "packing",
        data() {
          return {
              ps: null,
              width: 150,
              height: 75,
              approachAlgorithm: "0",
              randomShape: false,
              regularity: 36,
              dialog: false,
              polygons: [],
              show: false,
              dialogInfo: false,
              polygon: {
                  points: []
              },
              packing: {
                  height: 0,
                  width: 0,
                  polygons: []
              }
          }
        },
        dictionary: {
            custom: {
                width: {
                    required: () => 'Width is required to execute.',
                    min_value: 'Width must be greater than 0'
                },
                height: {
                    required: 'Height is required to execute.',
                    min_value: 'Width must be greater than 0'
                },
                regularity: {
                    min_value: 'regularity must be greater than 0'
                }
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
                    p.frameRate(60);
                    //console.log(canvas);
                };

                p.windowResized = () => {
                    if(typeof this.$refs.polygonContainer !== "undefined") {
                        p.resizeCanvas(this.$refs.polygonContainer.clientWidth, this.$refs.polygonContainer.clientHeight)
                    }
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(255,255,255);
                    p.noFill();
                    p.push();
                    this.packing.polygons.forEach(pol => {this.drawPolygon(pol, this.packing.width, this.packing.height, p)});
                    p.pop();
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainer');
            if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
            if (localStorage.getItem('packing')) this.packing = JSON.parse(localStorage.getItem('packing'));

        },
        watch: {
            packing: {
                handler() {
                    localStorage.setItem('packing', JSON.stringify(this.packing));
                },
                deep: true
            },
        },
        methods: {
            mouseInsidePolygon(polygon, x, y, width, height, p) {
                // http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
                let inside = false;
                for(let i = 0; i < polygon.points.length; i++) {
                    let xi = (polygon.points[i].x/ width) * p.width,
                        yi = ((height - polygon.points[i].y) / height) * p.height;
                    let xj = (polygon.points[(i + 1) % polygon.points.length].x / width) * p.width,
                        yj = ((height - polygon.points[(i + 1) % polygon.points.length].y) / height) * p.height;

                    let intersect = ((yi > y) !== (yj > y))
                        && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
                    if (intersect) inside = !inside;
                }
                return inside;
            },
            exportPacking(){

                let file = '';
                let points = {};
                let edges = {};
                let polygons = {};
                let p = 1;
                let e = 1;
                let polCount = 1;

                this.packing.polygons.forEach(pol => {

                    let polygonPoints = [];

                    for(let i = 0; i < pol.points.length; i++) {

                        let pointA = pol.points[i];
                        let pointB = pol.points[(i + 1) % pol.points.length];
                        if(!([pointA.x,pointA.y] in points)) {
                            points[[pointA.x,pointA.y]] = p;
                            p += 1;
                        }

                        if(!([pointB.x,pointB.y] in points)) {
                            points[[pointB.x,pointB.y]] = p;
                            p += 1;
                        }

                        if(!([points[[pointA.x,pointA.y]],points[[pointB.x,pointB.y]]] in edges)) {
                            edges[[points[[pointA.x,pointA.y]],points[[pointB.x,pointB.y]]]] = e;
                            e += 1;
                        }
                        polygonPoints.push(points[[pointA.x,pointA.y]]);
                    }

                    if(!(polygonPoints in polygons)) {
                        polygons[polygonPoints] = polCount;
                        polCount += 1;
                    }
                });

                let sortedPoints = this.sortDictionary(points);
                let sortedEdges = this.sortDictionary(edges);
                let sortedPolygons = this.sortDictionary(polygons);

                file += Object.keys(points).length + ' ' + Object.keys(edges).length + ' ' + Object.keys(polygons).length +'\n';

                sortedPoints.forEach(point => {
                    let splitted = point[0].split(",");
                    file += splitted[0] + ' ' + splitted[1] + '\n';
                });

                sortedEdges.forEach(edge => {
                    let splitted = edge[0].split(",");
                    file += splitted[0] + ' ' + splitted[1] + '\n';
                });

                sortedPolygons.forEach(polygon => {
                    let splitted = polygon[0].split(",");
                    splitted.forEach(s => {
                        file += (s + ' ');
                    });
                    file = file.slice(0,-1);
                    file += '\n';
                });


                let filename = 'packing.txt';
                let universalBOM = "\uFEFF";

                let link = document.createElement('a');
                link.setAttribute('href',  'data:text/csv; charset=utf-8,' + encodeURIComponent(universalBOM+file));
                link.setAttribute('download', filename);
                link.click();

            },
            sortDictionary(dict){
                // Create items array
                let sorted = Object.keys(dict).map(function(key) {
                    return [key, dict[key]];
                });

                // Sort the array based on the second element
                sorted.sort(function(first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
            showPolygonData(){
                this.dialogInfo = true;
            },
            drawPolygon(polygon, width, height, p) {
                p.stroke(33,33,33);
                if (this.polygon === polygon || (this.mouseInsidePolygon(polygon, p.mouseX, p.mouseY, width, height, p) && !this.dialogInfo)) {
                    p.fill(77,182,172);
                    if(!this.dialogInfo) {
                        this.polygon = polygon;
                    }
                } else {
                    p.noFill();
                }
                p.beginShape();
                polygon.points.forEach(pnt => {
                    let sx = (pnt.x / width) * p.width;
                    let sy = ((height - pnt.y) / height) * p.height;
                    p.vertex(sx, sy);
                });
                p.endShape(p.CLOSE);
            },
            execute(){
                this.$validator.validateAll().then(result => {
                    if(result) {
                        if(this.polygons.length === 0) {
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
                                'regularity': parseInt(this.regularity),
                                'approachAlgorithm': parseInt(this.approachAlgorithm)
                            };
                            api.sendMesh(data).then(resp => {
                                //console.log(resp);
                                this.packing = resp.body.mesh
                            }).catch(error => {
                                //console.log(error);
                                alert("Error executing algorithm.");
                            });
                        }
                        this.dialog = false;
                    }
                });
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
        padding:0;
        margin:0;
        height: 90%;
        min-width: 100%;
    }
</style>