<template>
    <div class="fill-height">
        <v-toolbar  dark>
            <v-toolbar-title>Packing</v-toolbar-title>
            <v-divider class="mx-2" inset vertical></v-divider>
            <v-spacer></v-spacer>
            <v-dialog v-model="dialog" max-width="500px">
                <v-btn slot="activator" color="teal darken-1" dark class="mb-2">Create New Packing</v-btn>
                <v-card>
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
                        <v-btn color="blue darken-1" flat @click.native="execute">Execute Algorithm</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>

        </v-toolbar>
        <div ref="polygonContainer" class="polygon">
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
              polygons: []
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
                    canvas = p.createCanvas(this.$refs.polygonContainer.clientWidth,this.$refs.polygonContainer.clientHeight);
                    canvas.parent(this.$refs.polygonDrawer);
                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(60);
                    //console.log(canvas);
                };
                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(255);
                    p.push();
                    p.translate(p.width*0.5, p.height*0.5);
                    p.rotate(p.frameCount / 200.0);
                    this.drawPolygon(0, 0, this.radius, this.numberOfVertex, p);
                    p.pop();
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script);
            if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));

        },
        methods: {
            drawPolygon(x, y, radius, npoints, p) {
                const angle = p.TWO_PI / npoints;
                p.beginShape();
                for (let a = 0; a < p.TWO_PI; a += angle) {
                    const sx = x + p.cos(a) * radius;
                    const sy = y + p.sin(a) * radius;
                    p.vertex(sx, sy);
                }
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
                                console.log(resp);
                            }).catch(error => {
                                console.log(error);
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

    .polygon {
        height: 100%;
        min-width: 100%;
    }
</style>