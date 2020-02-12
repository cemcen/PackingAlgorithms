<template>
    <v-card color="#ffffff" class="ma-3 fill-height" style="max-height: 80%">
        <v-card-title>
            <span class="headline">Upload results file</span>
            <v-spacer/>
            <v-btn dark color="primary" @click.native="uploadResultsFile()" @keyup.enter="uploadResultsFile">
                <v-icon class="mr-1">mdi-file-chart</v-icon>
                Upload File
            </v-btn>
        </v-card-title>
        <v-form ref="resultsFileForm" class="pl-4 pr-4">
            <v-row justify="center">
                <v-col class="pa-0 pr-3 pl-3">
                    <v-file-input v-model="file" accept=".txt" prepend-icon="mdi-file-chart"
                                  :rules="[validation.required()]" show-size
                                  label="Upload results file"></v-file-input>
                </v-col>
            </v-row>
        </v-form>
        <v-card-text style="max-height: 75%; overflow: auto;">
            <v-row justify="center">
                <v-list style="width: 100%; display: contents;" two-line>
                    <div class="text-centered font-weight-light grey--text title mb-2"
                         v-show="results.length === 0">
                        No results loaded yet.
                    </div>
                    <template v-for="(item, index) in results">

                        <v-list-item :key="index">

                            <v-list-item-action>
                                <v-checkbox color="teal lighten-2"  @change="selectedItem(item)"
                                            v-model="item.selected"></v-checkbox>
                            </v-list-item-action>

                            <v-list-item-content>
                                <v-list-item-title v-html="item.name"></v-list-item-title>
                                <v-list-item-subtitle>
                                    <span>Type: {{ item.typeName }}</span>
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                        <v-divider></v-divider>
                    </template>
                </v-list>
            </v-row>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from './../../services/validation.service';

    export default {
        name: "UploadResultsFile",
        data() {
            return {
                validation: validation,
                file: null,
                results: [],
            }
        },
        props: {
            polygons: {
                type: Array,
                defaultValue: []
            }
        },
        computed: {
            packing() {
                return this.$store.getters.getPacking;
            }
        },
        methods: {
            clearResults() {
                this.results = [];
                this.file = null;
                this.$refs.resultsFileForm.resetValidation();
            },
            uploadResultsFile() {
                if(this.$refs.resultsFileForm.validate()) {
                    if (this.file.type === 'text/plain') {
                        this.file.text().then(res => {
                            this.results = [];
                            try {
                                let lines = res.split("\n");
                                let typeName = lines[0].split(" ")[1];
                                let componentsNames = lines[2].split(" ");
                                componentsNames = componentsNames.splice(1, componentsNames.length - 1);
                                let numberOfVertices = parseInt(lines[3].split(" ")[1]);
                                lines = lines.splice(5, lines.length - 5);
                                let resultsPerComponent = Array(componentsNames.length).fill(undefined).map((_, i) => {
                                    return {
                                        name: componentsNames[i],
                                        type: 0,
                                        typeName: typeName,
                                        vertices: [],
                                        minValue: Number.MAX_VALUE,
                                        maxValue: 0
                                    }
                                });
                                for(let i = 0; i < numberOfVertices ; i++) {
                                    let line = lines[i].split(' ').filter(word => word.length > 0);
                                    line.forEach((ele, index) => {
                                        if(index > 0) {
                                            let cValue = parseFloat(ele);
                                            if (resultsPerComponent[index - 1].minValue > cValue) resultsPerComponent[index - 1].minValue = cValue;
                                            if (resultsPerComponent[index - 1].maxValue < cValue) resultsPerComponent[index - 1].maxValue = cValue;
                                            resultsPerComponent[index - 1].vertices.push({
                                                index: parseInt(line[0]),
                                                value: cValue
                                            })
                                        }
                                    });
                                }
                                this.results = this.results.concat(resultsPerComponent);

                                lines = lines.splice(numberOfVertices, lines.length - numberOfVertices);
                                typeName = lines[0].split(" ")[1];
                                componentsNames = lines[2].split(" ");
                                componentsNames = componentsNames.splice(1, componentsNames.length - 1);
                                let numberOfPolygons = parseInt(lines[3].split(" ")[1]);
                                lines = lines.splice(5, lines.length - 5);
                                resultsPerComponent = Array(componentsNames.length).fill(undefined).map((_, i) => {
                                    return {
                                        name: componentsNames[i],
                                        type: 1,
                                        typeName: typeName,
                                        polygons: [],
                                        minValue: Number.MAX_VALUE,
                                        maxValue: 0
                                    }
                                });
                                for(let i = 0; i < numberOfPolygons ; i++) {
                                    let line = lines[i].split(' ').filter(word => word.length > 0);
                                    line.forEach((ele, index) => {
                                        if(index > 0) {
                                            let cValue = parseFloat(ele);
                                            if(resultsPerComponent[index - 1].minValue > cValue) resultsPerComponent[index - 1].minValue = cValue;
                                            if(resultsPerComponent[index - 1].maxValue < cValue) resultsPerComponent[index - 1].maxValue = cValue;
                                            resultsPerComponent[index - 1].polygons.push({
                                                index: parseInt(line[0]),
                                                value: cValue
                                            });
                                        }
                                    });

                                }
                                this.results = this.results.concat(resultsPerComponent);
                            } catch (e) {
                                console.log(e);
                                this.$toast("Cannot load file (wrong format)");
                            }
                        }).catch(error => {
                            console.log(error);
                            this.$toast("Cannot load file (wrong format)");
                        })
                    } else {
                        this.$toast("File not supported");
                    }
                }
            },
            selectedItem(item) {
                if(item.selected) {
                    let index = this.results.indexOf(item);
                    this.results.forEach((r, i) => {
                        if(i !== index) {
                            r.selected = false;
                        }
                    });
                    this.loadResults(item);
                }

                this.$forceUpdate();
            },
            convertToRGB(hex) {
                const color = [];
                color[0] = parseInt((trim(hex)).substring(0, 2), 16);
                color[1] = parseInt((trim(hex)).substring(2, 4), 16);
                color[2] = parseInt((trim(hex)).substring(4, 6), 16);
                return color;
            },
            generateColor(colorStart, colorEnd, colorCount) {

                // The beginning of your gradient
                const start = this.convertToRGB(colorStart);
                // The end of your gradient
                const end = this.convertToRGB(colorEnd);
                // The number of colors to compute
                const len = colorCount;
                //Alpha blending amount
                let alpha = 0.0;
                const saida = [];
                for (i = 0; i < len; i++) {
                    var c = [];
                    alpha += (1.0 / len);

                    c[0] = start[0] * alpha + (1 - alpha) * end[0];
                    c[1] = start[1] * alpha + (1 - alpha) * end[1];
                    c[2] = start[2] * alpha + (1 - alpha) * end[2];

                    saida.push(c);
                }
                return saida;
            },
            loadResults(item) {
                let colors = [];
                let minHue = 240, maxHue=0;
                let number = 12;
                for (const x of Array(number + 1).keys()) {
                    colors.push((x/number)*(maxHue - minHue) + minHue)
                }
                if(item.type === 0) {
                    item.vertices.forEach(vert => {
                        let min = item.minValue;
                        let max = item.maxValue;
                        vert.color = ((vert.value - min) / (max - min)) * (maxHue - minHue) + minHue;
                    });

                    this.polygons.forEach(pol => {
                        pol.setVertexColors(item.vertices);
                    });

                } else if (item.type === 1) {
                    item.polygons.forEach(pol => {
                        let min = item.minValue;
                        let max = item.maxValue;
                        let value = ((pol.value - min) / (max - min)) * (maxHue - minHue) + minHue;
                        let hsl = parseInt(Math.round((value - minHue)/(maxHue - minHue)*number));
                        this.polygons.find(polShape => polShape.getIndex() === (pol.index)).setColor(pol.value, colors[hsl]);
                    });

                }

                this.$emit("reDraw");

            }
        }
    }
</script>

<style scoped>

</style>