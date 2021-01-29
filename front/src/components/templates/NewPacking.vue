<template>
    <v-card color="#ffffff" class="elevation-0">
        <v-tabs v-model="tab" slider-color="teal lighten-2" fixed-tabs color="teal lighten-2"
                background-color="transparent">
            <v-tab v-for="item in piledOption" :key="item">
                {{ item }}
            </v-tab>
        </v-tabs>
        <v-card-title>
            <v-row sm="12" justify="center">

                <v-btn v-if="tab === 0" dark color="teal lighten-2" :loading="loading" @click.native="execute">
                    <v-icon class="mr-1">mdi-cloud-sync</v-icon>
                    Run
                </v-btn>
                <v-btn v-else dark color="teal lighten-2" :loading="loading" @click.native="executeMultiLayer">
                    <v-icon class="mr-1">mdi-cloud-sync</v-icon>
                    Run
                </v-btn>
            </v-row>
        </v-card-title>
        <v-card-text style="max-height: 60vh; overflow: auto">
            <div v-show="tab === 0">
                <v-container>
                    <v-form ref="singlePackingForm">
                        <v-row justify="center">
                            <v-text-field v-model="width" type="number"
                                          :rules="[validation.required(), validation.requiredPositive()]"
                                          label="Container Width" clearable required>
                            </v-text-field>
                            <v-text-field v-model="height" type="number"
                                          :rules="[validation.required(), validation.requiredPositive()]"
                                          label="Container Height" clearable required>
                            </v-text-field>
                            <v-text-field v-model="regularity" type="number" label="Polygons Regularity"
                                          :rules="[validation.required(), validation.betweenValues(0, 100)()]"
                                          hint="Variability of an edge (5% of variability)"
                                          persistent-hint clearable>
                            </v-text-field>
                        </v-row>
                    </v-form>
                </v-container>
            </div>
            <div v-show="tab === 1">
                <v-container>
                    <v-form ref="multiplePackingForm">
                        <v-row justify="center">
                            <v-text-field v-model="width" type="number" label="Container Width"
                                          :rules="[validation.required(), validation.requiredPositive()]"
                                          clearable required>
                            </v-text-field>
                        </v-row>
                        <v-row v-for="(item, index) in layers" :key="index">
                            <v-text-field v-model="layers[index].height" type="number"
                                          :rules="[validation.required(), validation.requiredPositive()]"
                                          label="Container Height" clearable required>
                            </v-text-field>
                            <v-text-field v-model="layers[index].regularity" type="number"
                                          :rules="[validation.required(), validation.betweenValues(0, 100)()]"
                                          label="Polygons Regularity"
                                          hint="Variability of an edge (5% of variability)"
                                          persistent-hint clearable>
                            </v-text-field>
                            <v-select
                                    v-model="layers[index].polygons" :items="polygons"
                                    :rules="[validation.required(), validation.atLeastOneSelected()]"
                                    item-text="label" item-color="teal lighten-2"
                                    color="teal lighten-2" chips return-object
                                    label="Layer Polygons" multiple>
                            </v-select>
                        </v-row>
                        <v-row justify="center">
                            <v-btn color="teal lighten-2" class="mr-2" dark @click="addLayer">
                                <v-icon>{{icons['mdi-plus']}}</v-icon>
                            </v-btn>
                            <v-btn :disabled="layers.length === 1" :dark="layers.length !== 1"
                                   color="teal lighten-2"
                                   @click="deleteLayer">
                                <v-icon>{{icons['mdi-minus']}}</v-icon>
                            </v-btn>
                        </v-row>
                    </v-form>
                </v-container>
            </div>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from './../../services/validation.service';
    import {mdiPlus, mdiMinus} from '@mdi/js';

    export default {
        name: "NewPacking",
        props: {
            loading: {
                type: Boolean,
                defaultValue: false,
            },
        },
        data() {
            return {
                icons: {
                    "mdi-plus": mdiPlus,
                    "mdi-minus": mdiMinus,
                },
                validation: validation,
                piledOption: ["Single", "Multiple"],
                tab: 0,
                layers: [{
                    height: 75,
                    regularity: 5
                }],
                width: 150,
                height: 75,
                randomShape: false,
                regularity: 5,
                approach: 0,
            }
        },
        created() {
            this.validation.changeLanguage('en');
        },
        computed: {
            polygons() {
                return this.$store.getters.getPolygons;
            }
        },
        methods: {
            addLayer() {
                this.layers.push({
                    height: null,
                    regularity: 5,
                    polygons: []
                });
            },
            deleteLayer() {
                this.layers.pop();
            },
            resetValidation() {
                this.$refs.singlePackingForm.resetValidation();
                this.$refs.multiplePackingForm.resetValidation();
            },
            clearForm() {
                this.tab = 0;
                this.layers = [{
                    height: 75,
                    regularity: 5,
                    polygons: []
                }];
                this.width = 150;
                this.height = 75;
                this.randomShape = false;
                this.regularity = 5;
                this.approach = 0;
            },
            closeDialog() {
                this.resetValidation();
                this.clearForm();
                this.$emit("close");
            },
            execute() {
                if (this.$refs.singlePackingForm.validate()) {
                    this.$emit("execute", {
                        width: this.width,
                        height: this.height,
                        randomShape: this.randomShape,
                        regularity: this.regularity,
                        approach: this.approach
                    });
                }
            },
            executeMultiLayer() {
                if (this.$refs.multiplePackingForm.validate()) {
                    this.$emit("execute-multi-layer", {
                        width: this.width,
                        randomShape: this.randomShape,
                        regularity: this.regularity,
                        approach: this.approach,
                        layers: this.layers
                    });
                }
            }
        }
    }
</script>

<style scoped>

</style>