<template>
    <div class="page-container fill-height">
        <v-toolbar  dark>
            <v-toolbar-title>Packing</v-toolbar-title>
            <v-divider class="mx-2" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <v-dialog v-model="dialog" max-width="500px">
                <v-btn slot="activator" color="teal darken-1" dark class="mb-2">Create New Packing</v-btn>
                <v-card color="grey darken-4">
                    <v-card-title>
                        <span class="headline">New Packing</span>
                    </v-card-title>

                    <v-card-text>
                        <v-layout justify-center>
                            <v-flex>
                                <v-text-field v-validate="'required|min_value:1'"
                                              :error-messages="errors.collect('width')"
                                              v-model="width"
                                              type="number"
                                              label="Container Width"
                                              data-vv-name="width"
                                              clearable
                                              required>
                                </v-text-field>
                                <v-text-field v-validate="'required|min_value:1'"
                                              :error-messages="errors.collect('height')"
                                              v-model="height"
                                              type="number"
                                              label="Container Height"
                                              data-vv-name="height"
                                              clearable
                                              required>
                                </v-text-field>
                            </v-flex>
                        </v-layout>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="teal darken-1" flat @click.native="execute">Execute Algorithm</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>

            <v-dialog v-model="dialogInfo" max-width="500px">
                <v-card color="grey darken-4" >
                    <v-card-title class="headline grey darken-4" primary-title>
                        Propiedades del polígono
                    </v-card-title>

                    <v-card-text>
                        Número de vértices: {{this.polygon.points.length}}
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
        name: "Packing",
        data() {
          return {
              ps: null,
              width: null,
              height: null,
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
            }
        },
        mounted() {
            this.script = p => {
                let canvas = null;
                // Settings of the canvas.
                p.setup = () => {
                    // We use the div size as the canvas size.
                    // console.log(this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas = p.createCanvas(this.$refs.polygonContainer.clientWidth, p.windowHeight/1.6);//this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas.parent(this.$refs.polygonDrawer);
                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(60);
                    //console.log(canvas);
                };

                p.windowResized = () => {
                    if(typeof this.$refs.polygonContainer !== "undefined") {
                        p.resizeCanvas(this.$refs.polygonContainer.clientWidth, p.windowHeight / 1.6)
                    }
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(40,40,40);
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
                        yi = (polygon.points[i].y / height) * p.height;
                    let xj = (polygon.points[(i + 1) % polygon.points.length].x / width) * p.width,
                        yj = (polygon.points[(i + 1) % polygon.points.length].y / height) * p.height;

                    let intersect = ((yi > y) !== (yj > y))
                        && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
                    if (intersect) inside = !inside;
                }
                return inside;
            },
            showPolygonData(){
                this.dialogInfo = true;
            },
            drawPolygon(polygon, width, height, p) {
                p.stroke(0, 137, 123);
                if (this.polygon === polygon || (this.mouseInsidePolygon(polygon, p.mouseX, p.mouseY, width, height, p) && !this.dialogInfo)) {
                    p.fill(0, 137, 123);
                    if(!this.dialogInfo) {
                        this.polygon = polygon;
                    }
                } else {
                    p.noFill();
                }
                p.beginShape();
                polygon.points.forEach(pnt => {
                    let sx = (pnt.x / width) * p.width;
                    let sy = (pnt.y / height) * p.height;
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
                                'height': parseFloat(this.height)
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
        }
    }
</script>

<style scoped>
    .page-container {
        padding: 0;
        margin: 0;
    }
    .polygon {
        padding:0;
        margin:0;
        height: 100%;
        min-width: 100%;
    }
</style>