<template>
    <v-dialog eager v-model="dialog" fullscreen persistent max-width="500px" style="max-height: 100%">
        <v-toolbar dark color="primary">
            <v-btn icon dark @click="closeDialog()">
                <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-toolbar-title>Results</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip left>
                <template v-slot:activator="{ on }">
                    <v-btn icon color="white" v-on="on" @click="downloadImage">
                        <v-icon>mdi-file-image</v-icon>
                    </v-btn>
                </template>
                <span>Download Image</span>
            </v-tooltip>
        </v-toolbar>
        <v-card class="fill-height">
            <v-row class="fill-height" no-gutters>
                <v-col md="6" class="fill-height">
                    <!--<div id='myContainerUR' ref="pCont" class="polygon">
                        <div id="polygonDrawerUpdateResults" ref="polygonDrawer"></div>
                    </div>-->
                    <div id='myContainerKonvas' ref="pKonvas" style="height: 80%; width: 100%">

                    </div>
                </v-col>
                <v-col md="6" class="fill-height">
                    <upload-results-file :polygons="polygonsShape"/>
                </v-col>
            </v-row>
        </v-card>
    </v-dialog>
</template>

<script>

    import Constant from "../geometry/constants";
    import UploadResultsFile from "./UploadResultsFile.vue";
    import Polygon from "../geometry/polygon";

    export default {
        name: "UploadResultsDialog",
        components: {UploadResultsFile},
        data() {
            return {
                dialog: false,
                drawPacking: false,
                ps: null,
                stage: null,
                polygonsShape: [],
                configStage: {}
            }
        },
        mounted() {

            this.stage = new Konva.Stage({
                container: 'myContainerKonvas',
                width: 100,
                height: 100
            });

            this.createNewPacking();

            // adapt the stage on any window resize
            window.addEventListener('resize', () => {
                let container = this.$refs.pKonvas;

                this.stage.height(container.clientHeight);
                this.stage.width(container.clientWidth);
                this.stage.draw();
            });
        },
        computed: {
            packing() {
                return this.$store.getters.getPacking;
            }
        },
        watch: {
            dialog(val){
                if(val) {
                    this.reDraw();
                }
            }
        },
        methods: {
            openDialog() {
                this.dialog = true;
            },
            closeDialog() {
                this.dialog = false;
                this.$emit("closedDialog");
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
            downloadURI(uri, name) {
                let link = document.createElement('a');
                link.download = name;
                link.href = uri;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            },
            downloadImage() {
                let filename = 'results.png';
                let dataURL = this.stage.toCanvas({ pixelRatio: 3 });
                dataURL.toBlob((blob) => {
                    this.downloadFile(blob,filename, 'png');
                });
            },
            reDraw() {
                setTimeout(() => {
                    this.drawNewPacking();
                }, 310);

            },
            getWidth(p) {
                return p.width - Constant.WIDTH_OFFSET;
            },
            getHeight(p) {
                return p.height - Constant.HEIGHT_OFFSET;
            },
            getOffsetXAxis() {
                return Constant.X_OFFSET;
            },
            getOffsetYAxis() {
                return Constant.Y_OFFSET;
            },
            drawNewPacking() {
                window.dispatchEvent(new Event('resize'));
            },
            createNewPacking() {
                if (this.packing && this.packing.polygons) {
                    this.stage.destroyChildren();
                    let layer = new Konva.Layer();
                    this.polygonsShape = [];
                    this.packing.polygons.forEach(pol => {
                        this.polygonsShape.push(new Polygon(pol, this.packing.width, this.packing.height, this.stage, layer));
                    });
                    this.stage.add(layer);
                }
            },
            pointInsidePolygon(polygon, mousePoint, width, height, p) {
                let intersections = 0;
                for (let i = 0; i < polygon.points.length; i++) {

                    let pntA = polygon.points[i];
                    let pntB = polygon.points[(i + 1) % polygon.points.length];
                    let xi = ((pntA.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yi = (((height - pntA.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();
                    let xj = ((pntB.x / width) * this.getWidth(p)) + this.getOffsetXAxis(),
                        yj = (((height - pntB.y) / height) * this.getHeight(p)) + this.getOffsetYAxis();

                    if (this.vectorIntersection(xi, yi, xj, yj, mousePoint[0], mousePoint[1], -1000, -1000)) {
                        intersections += 1;
                    }
                }

                return intersections % 2 !== 0;
            },
            vectorIntersection(a, b, c, d, p, q, r, s) {
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
        }
    }
</script>

<style scoped>
    .polygon {
        padding: 0;
        margin: 0;
        height: 80%;
        min-width: 100%;
    }

    .hint-style {
        font-size: smaller;
        color: #666;
    }
</style>