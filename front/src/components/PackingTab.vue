<template>
    <div class="page-container">
        <v-layout class="all-height">
            <v-flex sm9 pa-2>
                <v-card class="my-card">
                    <v-toolbar color="#eeeeee">
                        <v-toolbar-title>Packing</v-toolbar-title>
                        <v-divider class="mx-2" inset vertical></v-divider>

                        <v-tooltip top>
                            <v-btn slot="activator" icon flat color="teal lighten-2" :disabled="executing" @click="dialog = true" dark class="mb-2">
                                <v-icon>add_circle_outline</v-icon>
                            </v-btn>
                            <span>Create New Packing</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <v-btn slot="activator" icon flat color="teal lighten-2" @click="openAssignProp" dark class="mb-2">
                                <v-icon>color_lens</v-icon>
                            </v-btn>
                            <span>Assign Properties</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <v-btn icon flat color="teal lighten-2" dark slot="activator" @click="exportPacking" class="mb-2">
                                <v-icon>save_alt</v-icon>
                            </v-btn>
                            <span>Export</span>
                        </v-tooltip>
                        <v-tooltip top>
                            <v-btn icon flat color="teal lighten-2" dark slot="activator" @click="downloadImage" class="mb-2">
                                <v-icon>image</v-icon>
                            </v-btn>
                            <span>Download Image</span>
                        </v-tooltip>

                        <v-spacer></v-spacer>



                        <v-progress-circular v-show="executing" indeterminate
                                             color="teal lighten-2"></v-progress-circular>
                        <v-dialog v-model="dialog" persistent max-width="500px">
                            <v-card color="#ffffff">
                                <v-card-title>
                                    <span class="headline">New Packing</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-layout justify-center row>
                                        <v-flex pr-3>
                                            <v-text-field v-validate="'required|min_value:1'"
                                                          :error-messages="errors.collect('width')"
                                                          v-model="width" type="number" label="Container Width"
                                                          data-vv-name="width" clearable required>
                                            </v-text-field>
                                        </v-flex>
                                        <v-flex>
                                            <v-text-field v-validate="'required|min_value:1'"
                                                          :error-messages="errors.collect('height')"
                                                          v-model="height" type="number" label="Container Height"
                                                          data-vv-name="height" clearable required>
                                            </v-text-field>
                                        </v-flex>
                                    </v-layout>
                                    <v-layout justify-center column>
                                        <v-flex>
                                            <v-text-field v-validate="'required|min_value:1|max_value:100'"
                                                          :error-messages="errors.collect('regularity')"
                                                          v-model="regularity" type="number"
                                                          label="Polygons Regularity"
                                                          hint="Variability of an edge (5% of variability)"
                                                          persistent-hint
                                                          data-vv-name="regularity" clearable>
                                            </v-text-field>
                                        </v-flex>
                                    </v-layout>
                                    <v-layout justify-center column>
                                        <v-flex>
                                            <v-radio-group v-validate="'required'" label="Choose approach"
                                                           :error-messages="errors.collect('approach')" v-model="approach"
                                                           data-vv-name="regularity" row>
                                                <v-radio color="teal lighten-2" label="Dense packing" :value="1"></v-radio>
                                                <v-radio color="teal lighten-2" label="Gravity simulation" :value="0"></v-radio>
                                            </v-radio-group>
                                        </v-flex>
                                    </v-layout>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" flat @click.native="dialog = false">Close</v-btn>
                                    <v-btn dark color="teal lighten-2" @click.native="execute">Execute Algorithm</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <v-dialog v-model="dialog2" persistent max-width="500px">
                            <v-card color="#ffffff">
                                <v-card-title>
                                    <span class="headline">Assign Properties</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-layout justify-center column>
                                        <v-flex>
                                            <v-select v-validate="'required'" label="Choose which polygons should have these properties"
                                                      :error-messages="errors.collect('selectedOptionProperties')" item-text="name"
                                                      v-model="selectedOptionProperties" :items="optionProperties" return-object
                                                      data-vv-name="selectedOptionProperties">
                                            </v-select>
                                        </v-flex>
                                    </v-layout>
                                    <v-layout justify-center column>
                                        <v-flex>
                                            <v-select v-validate="'required'" label="Choose which type of polygon should have these properties"
                                                      :error-messages="errors.collect('selectedOptionType')" item-text="name"
                                                      v-model="selectedOptionType" :items="optionType" return-object
                                                      data-vv-name="selectedOptionType">
                                            </v-select>
                                        </v-flex>
                                    </v-layout>
                                    <v-list two-line>
                                        <div class="text-centered font-weight-light grey--text title mb-2"
                                             v-show="properties.length === 0">
                                            No properties registered.
                                        </div>
                                        <template v-for="(key, value) in Object.entries(properties)">

                                            <v-list-tile :key="key[1].label" avatar @click="">

                                                <v-list-tile-action class="checkbox-width">
                                                    <v-checkbox color="teal lighten-2"
                                                                v-model="key[1].selected"></v-checkbox>
                                                </v-list-tile-action>

                                                <v-list-tile-avatar>
                                                    <swatches v-model="key[1].color" disabled colors="material-basic"/>
                                                </v-list-tile-avatar>

                                                <v-list-tile-content>
                                                    <v-list-tile-title v-html="key[1].label"></v-list-tile-title>
                                                    <v-list-tile-sub-title>
                                                        <span>Type of value: {{ key[1].typeOfValue }}</span> <br/>
                                                        <span>Default value: {{ key[1].default? key[1].default : 'Not Defined' }}</span>
                                                    </v-list-tile-sub-title>
                                                </v-list-tile-content>
                                            </v-list-tile>
                                            <v-divider></v-divider>
                                        </template>
                                    </v-list>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" flat @click.native="dialog2 = false">Close</v-btn>
                                    <v-btn dark color="teal lighten-2" @click.native="assignProperties">Assign Properties</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>

                        <!--<v-dialog v-model="dialogInfo" persistent max-width="500px">
                            <v-card color="#ffffff" >
                                <v-card-title>
                                    <span class="headline">Polygon Properties</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-layout column>
                                        <v-flex>
                                            <v-text-field disabled v-model="polygon.label"
                                                          label="Label">
                                            </v-text-field>
                                        </v-flex>
                                        <v-flex>
                                            <v-text-field disabled v-model="polygon.points.length"
                                                          label="Number of vertex">
                                            </v-text-field>
                                        </v-flex>
                                    </v-layout>
                                    <v-layout row v-for="property in polygon.properties" :key="property">
                                        <v-flex>
                                            <v-text-field disabled v-model="property.name"
                                                          label="Property">
                                            </v-text-field>
                                        </v-flex>
                                        <v-flex>
                                            <v-text-field disabled v-model="property.value"
                                                          label="Value">
                                            </v-text-field>
                                        </v-flex>
                                    </v-layout>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="teal lighten-2" flat @click.native="dialogInfo = false">Close</v-btn>
                                    <v-btn color="teal lighten-2" @keyup.enter="" @click.native="">Save</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog> -->
                    </v-toolbar>
                    <div id='myContainer' @click="this.showPolygonData" ref="polygonContainer" class="polygon">
                        <div ref="polygonDrawer"></div>
                    </div>
                </v-card>
            </v-flex>
            <v-flex sm3 pa-2>
                <v-card class="my-card">
                    <v-tabs show-arrows grow color="#eeeeee">
                        <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

                        <!--<v-tab id="tab-info" @click="selectTab(2)">
                            <v-icon>info</v-icon> Info
                        </v-tab>-->

                        <v-tab id="tab-polygon" @click="selectTab(1)">
                            <v-icon>category</v-icon> Polygons
                        </v-tab>

                        <v-tab id="tab-distribution" @click="selectTab(0)">
                            <v-icon>list_alt</v-icon> Properties
                        </v-tab>
                    </v-tabs>
                    <div v-show="selectedTab === 0" class="max-height-card">
                        <properties-tab @assign="assignProperties" ref="propertiesTab"></properties-tab>
                    </div>

                    <div v-show="selectedTab === 1" class="max-height-card">
                        <polygons-tab></polygons-tab>
                    </div>

                    <div v-show="selectedTab === 2" class="max-height-card">
                        <info-tab :selected-polygons="getSelectedPolygons"></info-tab>
                    </div>
                </v-card>
            </v-flex>
        </v-layout>

        <v-snackbar color="teal lighten-2" v-model="snackbar" bottom :timeout="timeout">
            {{ snackbarMessage }}
        </v-snackbar>
    </div>
</template>

<script>
    import PropertiesTab from "./PropertiesTab.vue";
    import PolygonsTab from "./PolygonsTab.vue";
    import InfoTab from "./InfoTab.vue";
    import api from "../services/api.services";
    import * as poly2tri from 'poly2tri';
    import Swatches from 'vue-swatches'

    // Import the styles too, globally
    import "vue-swatches/dist/vue-swatches.min.css"

    const routes = ["/properties", "/polygons", "/info"];

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        components: {InfoTab, PolygonsTab, PropertiesTab, Swatches},
        name: "packing",
        data() {
          return {
              selectedTab: 1,
              ps: null,
              snackbar: false,
              snackbarMessage: '',
              timeout: 1200,
              width: 150,
              height: 75,
              randomShape: false,
              regularity: 5,
              approach: 1,
              dialog: false,
              dialog2: false,
              polygons: [],
              optionProperties: [
                  {
                      value: 0,
                      name: "Selected Polygons"
                  },
                  {
                      value: 1,
                      name: "All Polygons"
                  }
              ],
              optionType: [
                  {
                      value: 0,
                      name: "All Polygons"
                  },
                  {
                      value: 1,
                      name: "Only polygons"
                  },
                  {
                      value: 2,
                      name: "Only holes"
                  }
              ],
              selectedOptionProperties: {
                  value: 0,
                  name: "Selected Polygons"
              },
              selectedOptionType: {
                  value: 0,
                  name: "All Polygons"
              },
              show: false,
              dialogInfo: false,
              executing: false,
              polygon: {
                  points: []
              },
              packing: {
                  height: 0,
                  width: 0,
                  polygons: [],
                  graph: {},
                  draw: {
                      points: {},
                      edges: {},
                      polygons: {},
                  }
              },
              properties: {},
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
                          min_value: 'Regularity must be at least 1',
                          max_value: 'Regularity can not be greater than 100%'
                      },
                      approach: {
                          required: 'Must select an approach.',
                      }
                  }
              },
          }
        },
        created() {
            this.selectTab(this.selectedTab);
        },
        computed: {
            getSelectedPolygons() {
                return this.packing.polygons.filter(pol => pol.selected);
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
                    p.frameRate(1);
                    //console.log(canvas);
                };

                p.windowResized = () => {
                    if(typeof this.$refs.polygonContainer !== "undefined") {
                        p.resizeCanvas(this.$refs.polygonContainer.clientWidth, this.$refs.polygonContainer.clientHeight)
                    }
                };

                let locked = false;
                let dragged = false;
                let xInit = 0;
                let yInit = 0;
                let bx = 0;
                let by = 0;
                p.mousePressed = () => {
                    if(p.mouseX > -10 && p.mouseY > -10 && p.mouseX < p.width + 10 && p.mouseY < p.height + 10 && !this.dialog2) {
                        locked = true;
                        xInit = p.mouseX;
                        yInit = p.mouseY;
                        bx = p.mouseX;
                        by = p.mouseY;
                    }
                };

                p.mouseDragged = () => {
                    if(locked) {
                        dragged = true;
                        p.draw();
                        bx = p.mouseX;
                        by = p.mouseY;
                    }
                };

                p.mouseReleased = () => {
                    locked = false;
                    if(dragged) {
                        let box = {
                            points: [
                                {
                                    x: Math.min(bx, xInit),
                                    y: Math.min(by, yInit)
                                },
                                {
                                    x: Math.max(bx, xInit),
                                    y: Math.min(by, yInit)
                                },
                                {
                                    x: Math.max(bx, xInit),
                                    y: Math.max(by, yInit)
                                },
                                {
                                    x: Math.min(bx, xInit),
                                    y: Math.max(by, yInit)
                                },
                            ]
                        };
                        let height = this.packing.height;
                        let width = this.packing.width;
                        this.packing.polygons.forEach(pol => {
                            for (let i = 0; i < pol.points.length; i++) {
                                let pntA = pol.points[i];
                                let pntB = pol.points[(i + 1) % pol.points.length];

                                if ([pntA.x, pntA.y] in this.packing.graph && [pntB.x, pntB.y] in this.packing.graph[[pntA.x, pntA.y]]) {
                                    this.packing.graph[[pntA.x, pntA.y]][[pntB.x, pntB.y]] = {
                                        selected: false
                                    }
                                } else if ([pntB.x, pntB.y] in this.packing.graph && [pntA.x, pntA.y] in this.packing.graph[[pntB.x, pntB.y]]) {
                                    this.packing.graph[[pntB.x, pntB.y]][[pntA.x, pntA.y]] = {
                                        selected: false
                                    }
                                }
                            }
                        });
                        this.packing.polygons.forEach(pol => {
                            pol.selected = this.polygonIntersection(pol, box, width, height, p);
                            for (let i = 0; i < pol.points.length; i++) {
                                let pntA = pol.points[i];
                                let pntB = pol.points[(i + 1) % pol.points.length];

                                if ([pntA.x, pntA.y] in this.packing.graph && [pntB.x, pntB.y] in this.packing.graph[[pntA.x, pntA.y]]) {
                                    this.packing.graph[[pntA.x, pntA.y]][[pntB.x, pntB.y]] = {
                                        selected: pol.selected || this.packing.graph[[pntA.x, pntA.y]][[pntB.x, pntB.y]].selected
                                    }
                                } else if ([pntB.x, pntB.y] in this.packing.graph && [pntA.x, pntA.y] in this.packing.graph[[pntB.x, pntB.y]]) {
                                    this.packing.graph[[pntB.x, pntB.y]][[pntA.x, pntA.y]] = {
                                        selected: pol.selected || this.packing.graph[[pntB.x, pntB.y]][[pntA.x, pntA.y]].selected
                                    }
                                }
                            }
                        });
                        dragged = false;
                        p.draw();
                    }

                    xInit = 0;
                    yInit = 0;
                    bx = 0;
                    by = 0;
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    p.background(255,255,255);
                    p.noFill();
                    p.push();
                    this.packing.polygons.forEach(pol => {this.drawPolygon(pol, this.packing.width, this.packing.height, p)});
                    let graph = this.packing.graph;
                    let height = this.packing.height;
                    let width = this.packing.width;
                    Object.keys(graph).forEach(function(pointA) {
                        Object.keys(graph[pointA]).forEach(function(pointB) {
                            const pntA = JSON.parse("[" + pointA + "]");
                            const pntB = JSON.parse("[" + pointB + "]");

                            if(graph[pointA][pointB].selected) {
                                p.stroke(189,189,189);
                            } else {
                                p.stroke(33, 33, 33);
                            }

                            if(graph[pointA][pointB].selected) {
                                p.strokeWeight(4);
                            } else {
                                p.strokeWeight(3);
                            }

                            p.line(
                                (pntA[0] / width) * p.width,
                                ((height - pntA[1]) / height) * p.height,
                                (pntB[0] / width) * p.width,
                                ((height - pntB[1]) / height) * p.height
                            );
                        });
                    });
                    if(locked) {
                        p.strokeWeight(3);
                        p.stroke(239,83,80);
                        p.noFill();
                        let x = Math.min(bx, xInit);
                        let y = Math.min(by, yInit);
                        p.rect(x,y, Math.abs(bx - xInit), Math.abs(by - yInit))
                    }
                    p.pop();
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script, 'myContainer');
            if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
            if (localStorage.getItem('properties')) this.properties = JSON.parse(localStorage.getItem('properties'));
            if (localStorage.getItem('packing')) {
                this.packing = JSON.parse(localStorage.getItem('packing'));
                this.packing.polygons.map(pol => pol.selected = false);
            }
            this.$validator.localize('es', this.dictionary);
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
            selectTab(i) {
                this.selectedTab = i;
                this.$router.push(routes[i]);
            },
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
                let points = this.packing.draw.points;
                let edges = this.packing.draw.edges;
                let polygons = this.packing.draw.polygons;

                let sortedPoints = this.sortDictionary(points);
                let sortedEdges = this.sortDictionary(edges);
                let sortedPolygons = this.sortPolygons(polygons);
                let properties = JSON.parse(localStorage.getItem('properties'));
                let propertiesArray = Object.keys(properties);

                file += Object.keys(points).length + ' ' + Object.keys(edges).length + ' ' + propertiesArray.length + ' ' + Object.keys(polygons).length + '\n';

                sortedPoints.forEach(point => {
                    let splitted = point[0].split(",");
                    file += splitted[0] + ' ' + splitted[1] + '\n';
                });

                sortedEdges.forEach(edge => {
                    let splitted = edge[0].split(",");
                    file += splitted[0] + ' ' + splitted[1] + '\n';
                });

                propertiesArray.forEach(prop => {
                    file += prop + '\n';
                });

                sortedPolygons.forEach(polygon => {
                    let splitted = polygon[0].split(",");
                    file += (splitted.length + ' ');
                    splitted.forEach(s => {
                        file += (s + ' ');
                    });
                    file += (polygon[2].area + ' ');
                    file += ((polygon[2].hole? 1: 0) + ' ');
                    file += (polygon[2].properties.length + ' ');

                    if(polygon[2].properties && polygon[2].properties.length > 0){
                        polygon[2].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    }
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
            sortPolygons(dict){
                // Create items array
                let sorted = Object.keys(dict).map(function(key) {
                    return [key, dict[key].count, dict[key].polygon];
                });

                // Sort the array based on the second element
                sorted.sort(function(first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
            showPolygonData(){
                this.selectedPolygon = this.polygon;
                //this.selectedPolygon.selected = !this.selectedPolygon.selected;
                if(this.polygon.properties == null) {
                    this.polygon.properties = [];
                }

                localStorage.setItem('packing', JSON.stringify(this.packing));
            },
            assignProperties() {
                this.packing.polygons.forEach(polygon => {
                    if ((this.selectedOptionProperties.value === 0 && polygon.selected) || this.selectedOptionProperties.value === 1) {
                        if(this.selectedOptionType.value === 0
                            || (this.selectedOptionType.value === 1 && !polygon.hole)
                            || (this.selectedOptionType.value === 2 && polygon.hole)) {
                            if (polygon.triangulation == null) {
                                polygon.triangulation = [];
                                let contour = [];
                                polygon.points.forEach(pnt => {
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

                                    polygon.triangulation.push(triangle);
                                });
                            }

                            polygon.properties = [];
                            let properties = this.properties;
                            Object.keys(properties).forEach(function (item) {
                                if (properties[item].selected) {
                                    polygon.properties.push({key: item, value: properties[item].default})
                                }
                            });

                            let polygonPoints = [];
                            for (let i = 0; i < polygon.points.length; i++) {
                                let pointA = polygon.points[i];
                                polygonPoints.push(this.packing.draw.points[[pointA.x, pointA.y]]);
                            }

                            this.packing.draw.polygons[polygonPoints].polygon.properties = polygon.properties;
                        }
                    }
                });

                this.ps.draw();
                this.dialog2 = false;

                //this.snackbar = true;
                //this.snackbarMessage = "Properties assigned successfully";
            },
            drawPolygon(polygon, width, height, p) {
                p.stroke(33, 33, 33);
                p.strokeWeight(1);
                if(polygon.properties != null && polygon.properties.length > 0) {
                    if(polygon.triangulation != null && polygon.triangulation.length >= polygon.properties.length) {
                        let properties = JSON.parse(localStorage.getItem('properties'));
                        polygon.properties = polygon.properties.filter(prop => Object.keys(properties).includes(prop.key));

                        if(polygon.properties.length > 0) {
                            let painted_triangles_each_step = Math.floor(polygon.triangulation.length / polygon.properties.length);
                            for (let i = 0; i < polygon.triangulation.length; i += 1) {
                                p.fill(properties[polygon.properties[(Math.floor(i / painted_triangles_each_step)) % polygon.properties.length].key].color);
                                p.stroke(properties[polygon.properties[(Math.floor(i / painted_triangles_each_step)) % polygon.properties.length].key].color);
                                p.beginShape();
                                polygon.triangulation[i].forEach(pnt => {
                                    let sx = (pnt.x / width) * p.width;
                                    let sy = ((height - pnt.y) / height) * p.height;

                                    p.vertex(sx, sy);
                                });
                                p.endShape(p.CLOSE);
                            }
                        }
                    }
                }
            },
            execute(){
                this.$validator.validateAll().then(result => {
                    if(result) {
                        if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
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
                                'regularity': parseInt(180 / parseInt(this.regularity)),
                                'approachAlgorithm': this.approach
                            };
                            this.executing = true;
                            api.sendMesh(data).then(resp => {
                                this.executing = false;
                                //console.log(resp);
                                this.packing = resp.body.mesh;

                                let points = {};
                                let edges = {};
                                let polygons = {};
                                let edgesG = {};
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

                                            if(pol.hole) {
                                                if (!([pointA.x, pointA.y] in edgesG)) {
                                                    edgesG[[pointA.x, pointA.y]] = {};
                                                }
                                                edgesG[[pointA.x, pointA.y]][[pointB.x, pointB.y]] = {
                                                    selected: false,
                                                    hover: false,
                                                };
                                            }
                                        }
                                        polygonPoints.push(points[[pointA.x,pointA.y]]);
                                    }

                                    if(!(polygonPoints in polygons)) {
                                        polygons[polygonPoints] = {
                                            count: polCount,
                                            polygon: pol
                                        };
                                        polCount += 1;
                                    }
                                });

                                this.packing.draw = {};
                                this.packing.draw.points = points;
                                this.packing.draw.edges = edges;
                                this.packing.draw.polygons = polygons;
                                this.packing.graph = edgesG;
                            }).catch(error => {
                                this.executing = false;
                                //console.log(error);
                                alert("Error executing algorithm.");
                            });
                        }
                        this.dialog = false;
                    }
                });
            },
            downloadImage() {
                let filename = 'packing.png';
                this.ps.save(filename);
            },
            polygonIntersection(polygon, box, width, height, p) {
                let intersects = false;
                for(let i = 0; i < polygon.points.length; i++) {

                    let pntA = polygon.points[i];
                    let pntB = polygon.points[(i + 1) % polygon.points.length];
                    let xi = (pntA.x/ width) * p.width,
                        yi = ((height - pntA.y) / height) * p.height;
                    let xj = (pntB.x / width) * p.width,
                        yj = ((height - pntB.y) / height) * p.height;
                    for(let j = 0; j < box.points.length; j++) {
                        let pntC = box.points[j];
                        let pntD = box.points[(j + 1) % box.points.length];
                        intersects = intersects || this.vectorIntersection(xi,yi,xj,yj, pntC.x, pntC.y, pntD.x, pntD.y);
                    }
                }

                if(!intersects) {
                    let pntA = polygon.points[0];
                    let x = (pntA.x/ width) * p.width,
                        y = ((height - pntA.y) / height) * p.height;
                    let inside = false;
                    for(let i = 0; i < box.points.length; i++) {
                        let xi = box.points[i].x,
                            yi = box.points[i].y;
                        let xj = box.points[(i + 1) % box.points.length].x,
                            yj = box.points[(i + 1) % box.points.length].y;

                        let intersect = ((yi > y) !== (yj > y))
                            && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
                        if (intersect) inside = !inside;
                    }
                    if(inside) intersects = true;
                }
                return intersects;
            },
            vectorIntersection(a,b,c,d,p,q,r,s) {
                let det, gamma, lambda;

                det = (c - a) * (s - q) - (r - p) * (d - b);
                if (det === 0) {
                    return false;
                } else {
                    lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
                    gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
                    return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
                }
            },
            openAssignProp() {
                if (localStorage.getItem('properties')) this.properties = JSON.parse(localStorage.getItem('properties'));
                this.dialog2 = true;
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

    .my-card{
        height: 100%;
    }

    .all-height{
        height: 100%;
    }

    .max-height-card {
        max-height: 600px;
        overflow: auto;
    }
</style>