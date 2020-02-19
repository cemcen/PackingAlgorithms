<template>
    <v-dialog eager v-model="dialog" persistent style="height: 93vh !important;">
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
        <v-card>
            <v-row no-gutters>
                <v-col md="6">
                    <div class="scene polygon" ref="scene">
                    </div>
                </v-col>
                <v-col md="6">
                    <upload-results-file ref="uploadResultsFileRef" :polygons="polygonsShape"/>
                </v-col>
            </v-row>
        </v-card>
    </v-dialog>
</template>

<script>

    import Constant from "../geometry/constants";
    import UploadResultsFile from "./UploadResultsFile.vue";
    import Polygon from "../geometry/polygon";
    import * as THREE from "three";


    export default {
        name: "UploadResultsDialog",
        components: {UploadResultsFile},
        data() {
            return {
                dialog: false,
                drawPacking: false,
                polygonsShape: [],
                configStage: {},
                renderer: new THREE.WebGLRenderer({preserveDrawingBuffer: true }),
                scene: new THREE.Scene(),
                camera: null
            }
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
            downloadFile(filename, type) {
                const e = document.createEvent('MouseEvents'),
                    a = document.createElement('a');
                a.download = filename;
                a.href = this.renderer.domElement.toDataURL();
                a.dataset.downloadurl = [type, a.download, a.href].join(':');
                e.initEvent("click", true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
                a.dispatchEvent(e);
            },
            downloadImage() {
                let filename = 'results.png';
                this.downloadFile(filename, 'png');
            },
            reDraw() {
                setTimeout(() => {
                    this.drawNewPacking();
                }, 310);
            },
            drawNewPacking() {
                window.dispatchEvent(new Event('resize'));
                this.scene.background = new THREE.Color( 0xffffff );
                /// THREEJS
                const el = this.$refs.scene;

                this.camera = new THREE.PerspectiveCamera(
                    50,
                    el.clientWidth / el.clientHeight,
                    0.1,
                    1000
                );

                this.renderer.setSize(el.clientWidth, el.clientHeight);
                el.appendChild(this.renderer.domElement);


                this.camera.position.z = 5;
                this.camera.position.x = 0;
                this.camera.position.y = 0;
                this.camera.lookAt (new THREE.Vector3 (0.0, 0.0, 0.0));
                console.log(this.packing.width);
                console.log(this.packing.height);
                this.camera.position.x = this.packing.width / 2;
                this.camera.position.y = this.packing.height / 2;

                this.packing.polygons.forEach(pol => {
                    const geometry = new THREE.Geometry();

                    pol.points.forEach(pnt => {
                        geometry.vertices.push( new THREE.Vector3(pnt.oldX,pnt.oldY,0));
                    });

                    for ( let i = 0; i< pol.points.length-2; i++) {
                        geometry.faces.push( new THREE.Face3(0,i+1,i+2));
                    }

                    const material = new THREE.MeshBasicMaterial({ color: 0x00ff00, side: THREE.FrontSide, wireframe: true });
                    const polygon = new THREE.Mesh(geometry, material);
                    this.scene.add(polygon);
                });



                const animate = () => {
                    requestAnimationFrame(animate);


                    this.renderer.render(this.scene, this.camera);
                };

                animate();
            },
        }
    }
</script>

<style scoped>
    .polygon {
        padding: 0;
        margin: 0;
        height: 75vh;
        min-width: 100%;
    }

    .hint-style {
        font-size: smaller;
        color: #666;
    }
</style>