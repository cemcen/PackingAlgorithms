<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">Download Packing</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="fileDownloadForm">
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-text-field v-model="filename" label="Filename"
                                      :rules="[validation.required()]"></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-select v-model="selectedOption" :items="fileDownloadOptions"
                                      item-text="type" label="File Format"
                                      :rules="[validation.required()]" return-object></v-select>
                        </v-col>
                    </v-row>
                </v-form>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                <v-btn dark color="teal lighten-2" @keyup.enter="downloadPacking" @click.native="downloadPacking">Download</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import validation from './../../services/validation.service';

    export default {
        name: "DownloadPacking",
        data() {
            return {
                dialog: false,
                validation: validation,
                file: null,
                filename: 'packing',
                fileDownloadOptions: [
                    {type: ".txt", value: 0},
                    {type: ".json", value: 1}
                ],
                selectedOption: {type: ".txt", value: 0},
            }
        },
        created() {
            this.validation.changeLanguage('en');
        },
        computed: {
            packing () {
                return this.$store.getters.getPacking;
            },
            properties () {
                return this.$store.getters.getProperties;
            },
            polygons () {
                return this.$store.getters.getPolygons;
            },
        },
        methods: {
            downloadPacking() {
                if (this.$refs.fileDownloadForm.validate()) {
                    if(this.selectedOption.value === 0) {
                        this.exportPackingTxt();
                    } else {
                        this.exportPackingJSON();
                    }
                    this.close();
                }
            },
            openDialog() {
                this.resetValidation();
                this.filename = 'packing';
                this.selectedOption = {type: ".txt", value: 0};
                this.dialog = true;
            },
            close() {
                this.resetValidation();
                this.dialog = false;
            },
            resetValidation() {
                this.$refs.fileDownloadForm.resetValidation();
            },
            exportPackingJSON(){
                let exportObj = {
                    packing: this.packing,
                    properties: this.properties,
                    polygons: this.polygons
                };
                let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(exportObj));
                let downloadAnchorNode = document.createElement('a');
                downloadAnchorNode.setAttribute("href", dataStr);
                downloadAnchorNode.setAttribute("download", this.filename + ".json");
                document.body.appendChild(downloadAnchorNode); // required for firefox
                downloadAnchorNode.click();
                downloadAnchorNode.remove();
            },
            exportPackingTxt(){
                let file = '';
                let points = this.packing.draw.points;
                let edges = this.packing.draw.edges;
                let polygons = this.packing.draw.polygons;
                let borderPoints = this.packing.draw.borderPoints;
                let borderSegments = this.packing.draw.borderSegments;

                let sortedPoints = this.sortDictionary(points);
                let sortedEdges = this.sortDictionary(edges);
                let sortedPolygons = this.sortPolygons(polygons);
                let properties = this.properties;
                if(!properties) properties = {};
                let propertiesArray = Object.keys(properties);

                file += Object.keys(points).length
                    + ' ' + Object.keys(edges).length
                    + ' ' + propertiesArray.length
                    + ' ' + Object.keys(polygons).length
                    + ' ' + Object.keys(borderPoints).length
                    + ' ' + Object.keys(borderSegments).length
                    + '\n';

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
                    file += ((polygon[2].hole ? 1 : 0) + ' ');

                    if (polygon[2].properties && polygon[2].properties.length > 0) {
                        file += (polygon[2].properties.length + ' ');
                        polygon[2].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    } else {
                        file += (0 + ' ');
                    }
                    file = file.slice(0, -1);
                    file += '\n';
                });

                Object.keys(borderPoints).forEach(bp => {
                    file += (borderPoints[bp].pointIndex + ' ');
                    if (borderPoints[bp].properties && borderPoints[bp].properties.length > 0) {
                        file += (borderPoints[bp].properties.length + ' ');
                        borderPoints[bp].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    } else {
                        file += (0 + ' ');
                    }
                    file = file.slice(0, -1);
                    file += '\n';
                });

                Object.keys(borderSegments).forEach(bs => {
                    file += (borderSegments[bs].segmentIndex + ' ');
                    borderSegments[bs].polygons.forEach(pol => {
                        file += (pol + ' ');
                        file += ((sortedPolygons[pol - 1][0].split(",").indexOf(Object.keys(edges)[borderSegments[bs].segmentIndex - 1].split(",")[0]) + 1)+ ' ');

                    });
                    if (borderSegments[bs].properties && borderSegments[bs].properties.length > 0) {
                        file += (borderSegments[bs].properties.length + ' ');
                        borderSegments[bs].properties.forEach(prop => {
                            file += ((propertiesArray.indexOf(prop.key) + 1) + ' ');
                        })
                    } else {
                        file += (0 + ' ');
                    }
                    file = file.slice(0, -1);
                    file += '\n';
                });


                let filename = this.filename + '.txt';
                let universalBOM = "\uFEFF";

                let link = document.createElement('a');
                link.setAttribute('href', 'data:text/csv; charset=utf-8,' + encodeURIComponent(universalBOM + file));
                link.setAttribute('download', filename);
                link.click();
            },
            sortDictionary(dict) {
                // Create items array
                let sorted = Object.keys(dict).map(function (key) {
                    return [key, dict[key]];
                });

                // Sort the array based on the second element
                sorted.sort(function (first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
            sortPolygons(dict) {
                // Create items array
                let sorted = Object.keys(dict).map(function (key) {
                    return [key, dict[key].count, dict[key].polygon];
                });

                // Sort the array based on the second element
                sorted.sort(function (first, second) {
                    return first[1] - second[1];
                });

                return sorted;
            },
        }
    }
</script>

<style scoped>

</style>