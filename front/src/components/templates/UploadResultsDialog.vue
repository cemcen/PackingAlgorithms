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
                    <div id='myContainerBC' ref="pCont" class="polygon">
                        <div id="polygonDrawerBorderConditions" ref="polygonDrawer"></div>
                    </div>
                </v-col>
                <v-col md="6" class="fill-height">

                </v-col>
            </v-row>
        </v-card>
    </v-dialog>
</template>

<script>

    export default {
        name: "UploadResultsDialog",
        data() {
            return {
                dialog: false,
            }
        },
        methods: {
            openDialog() {
                this.dialog = true;
            },
            closeDialog() {
                this.dialog = false;
            },
            downloadImage() {

            }
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