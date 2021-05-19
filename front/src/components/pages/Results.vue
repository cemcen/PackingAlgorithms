<template>
    <div>
        <v-toolbar dark color="primary">
            <v-toolbar-title>Results</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip left>
                <template v-slot:activator="{ on }">
                    <v-btn icon color="white" v-on="on" @click="downloadImage">
                        <v-icon>{{icons['mdi-file-image']}}</v-icon>
                    </v-btn>
                </template>
                <span>Download Image</span>
            </v-tooltip>
        </v-toolbar>
        <v-card>
            <v-row no-gutters>
                <v-col md="6" ref="col">
                    <div class="scene polygon" ref="scene"></div>
                </v-col>
                <v-col md="6">
                    <upload-results-file ref="uploadResultsFileRef" :polygons="packing.polygons"
                                         @reDraw="drawNewPacking"/>
                </v-col>
            </v-row>
        </v-card>
    </div>
</template>

<script>
    import UploadResultsFile from "../templates/UploadResultsFile.vue";
    import * as THREE from "three";
    import {mdiFileImage} from "@mdi/js";
    import {Lut} from "three/examples/jsm/math/Lut";
    export default {
        name: "Results",
        components: {UploadResultsFile},
        data() {
            return {
                icons: {
                    "mdi-file-image": mdiFileImage,
                },
                dialog: false,
                drawPacking: false,
                configStage: {},
                renderer: new THREE.WebGLRenderer({preserveDrawingBuffer: true }),
                scene: new THREE.Scene(),
                camera: null,
                height: null,
                width: null,
                selectedLut: 'rainbow',
                lutOptions: [ 'rainbow', 'cooltowarm', 'blackbody', 'grayscale' ],
                numberOfColors: 200,
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
        mounted() {
            setTimeout(() => {
                this.drawNewPacking({colorMap: 'rainbow', numberOfColors: 128});
            }, 310);
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
                if(this.renderer) {
                    let filename = 'results.png';
                    this.downloadFile(filename, 'png');
                }
            },
            reDraw(options) {
                setTimeout(() => {
                    this.drawNewPacking(options);
                }, 310);
            },
            drawNewPacking(options) {
                window.dispatchEvent(new Event('resize'));

                this.scene.background = new THREE.Color( 0xffffff );
                /// THREEJS
                const el = this.$refs.scene;
                const parent = this.$refs.col;

                if(!this.height) this.height = parent.clientHeight;
                if(!this.width) this.width = parent.clientWidth;
                this.renderer.setSize(this.width, this.height);
                el.appendChild(this.renderer.domElement);


                for( let i = this.scene.children.length - 1; i >= 0; i--) {
                    let obj = this.scene.children[i];
                    this.scene.remove(obj);
                }


                let maxX = this.packing.polygons[0].points[0].oldX;
                let minX = this.packing.polygons[0].points[0].oldX;
                let minY = this.packing.polygons[0].points[0].oldY;
                let maxY = this.packing.polygons[0].points[0].oldY;
                this.packing.polygons.forEach(pol => {
                    pol.points.forEach(pnt => {
                        if (pnt.x > maxX) maxX = pnt.x;
                        if (pnt.x < minX) minX = pnt.x;
                        if (pnt.y > maxY) maxY = pnt.y;
                        if (pnt.y < minY) minY = pnt.y;
                    });
                });


                this.camera = new THREE.PerspectiveCamera(
                    50,
                    1,
                    1,
                    1000
                );

                const lut = new Lut( options.colorMap, options.numberOfColors );

                const sprite = new THREE.Sprite( new THREE.SpriteMaterial( {
                    map: new THREE.CanvasTexture( lut.createCanvas() )
                } ) );

                sprite.scale.x = 0.125 * (maxX - minX) / 2.8;
                sprite.scale.y = (maxX - minX) / 2.8;
                sprite.position.set(maxX - minX + (maxX - minX) / 20, ((maxY + minY) / 2), 0);
                this.scene.add(sprite);

                this.camera.position.z = 4 * (maxX - minX) / 2.8;
                this.camera.lookAt (new THREE.Vector3 (0.0, 0.0, 0.0));
                this.camera.position.x = (maxX - minX) / 2;
                this.camera.position.y = (maxY - minY) / 2;

                lut.setMax(this.packing.polygons[0].points[0].max);
                lut.setMin(this.packing.polygons[0].points[0].min);

                this.packing.polygons.forEach(pol => {
                    const geometry = new THREE.Geometry();

                    pol.points.forEach(pnt => {
                        geometry.vertices.push( new THREE.Vector3(pnt.x - minX, pnt.y - minY,0));
                    });

                    const triangles = THREE.ShapeUtils.triangulateShape ( pol.points.map(pnt => new THREE.Vector2(pnt.x - minX, pnt.y - minY)), [] );

                    for( let i = 0; i < triangles.length; i++ ){

                        let face = new THREE.Face3( triangles[i][0], triangles[i][1], triangles[i][2]);
                        let color1 = [255,255,255], color2 = [255,255,255], color3 = [255,255,255];
                        if('value' in pol.points[triangles[i][0]]) {
                            let lColor = lut.getColor( pol.points[triangles[i][0]].value );
                            color1 = [parseInt(lColor.r * 255), parseInt(lColor.g * 255), parseInt(lColor.b * 255)];
                        }
                        if('value' in  pol.points[triangles[i][1]]) {
                            let lColor = lut.getColor( pol.points[triangles[i][1]].value );
                            color2 = [parseInt(lColor.r * 255), parseInt(lColor.g * 255), parseInt(lColor.b * 255)];
                        }
                        if('value' in  pol.points[triangles[i][2]]) {
                            let lColor = lut.getColor( pol.points[triangles[i][2]].value );
                            color3 = [parseInt(lColor.r * 255), parseInt(lColor.g * 255), parseInt(lColor.b * 255)];
                        }
                        face.vertexColors[0] = new THREE.Color("rgb(" + color1[0] + "," + color1[1]  + "," +  color1[2] + ")");
                        face.vertexColors[1] = new THREE.Color("rgb(" + color2[0] + "," + color2[1]  + "," +  color2[2] + ")");
                        face.vertexColors[2] = new THREE.Color("rgb(" + color3[0] + "," + color3[1]  + "," +  color3[2] + ")");
                        geometry.faces.push(face);
                    }

                    const material = new THREE.MeshBasicMaterial({  vertexColors: THREE.VertexColors, side: THREE.FrontSide});
                    const polygon = new THREE.Mesh(geometry, material);
                    this.scene.add(polygon);

                    const edges = new THREE.EdgesGeometry(geometry);
                    const mesh2 = new THREE.LineSegments(edges, new THREE.LineBasicMaterial({color:0x000000}));
                    this.scene.add(mesh2);
                });

                const arrowPos = new THREE.Vector3( -(maxX - minX) / 20, -(maxX - minX) / 20 ,0 );
                const arrowX = new THREE.ArrowHelper( new THREE.Vector3( 1,0,0 ), arrowPos, 0.2 * (maxX - minX) / 2.8, 0x7F2020, 0.0439568345323741 * (maxX - minX) / 2.8, 0.02197841726618705 * (maxX - minX) / 2.8 );

                this.scene.add(arrowX);
                this.scene.add( new THREE.ArrowHelper( new THREE.Vector3( 0,1,0 ), arrowPos, 0.2 * (maxX - minX) / 2.8, 0x207F20, 0.024420463629096723 * (maxX - minX) / 2.8, 0.0439568345323741 * (maxX - minX) / 2.8) );


                const animate = () => {
                    requestAnimationFrame(animate);
                    this.renderer.render(this.scene, this.camera);
                };

                animate();
                window.dispatchEvent(new Event('resize'));
            },
        }
    }
</script>

<style scoped>

</style>