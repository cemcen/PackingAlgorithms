<template>
    <v-dialog eager v-model="dialog" persistent max-width="700px">
        <v-card color="#ffffff">
            <v-card-title>
                <span class="headline">New Packing</span>
            </v-card-title>

            <v-tabs v-model="tab" slider-color="teal lighten-2" color="teal lighten-2"
                    background-color="transparent" grow>
                <v-tab v-for="item in piledOption" :key="item">
                    {{ item }}
                </v-tab>
            </v-tabs>
            <v-card-text>
                <div v-show="tab === 0">
                    <v-container>
                        <v-form ref="singlePackingForm">
                            <v-row justify="center">
                                <v-flex pr-3>
                                    <v-text-field v-model="width" type="number" :rules="[validation.required(), validation.requiredPositive()]"
                                                  label="Container Width" clearable required>
                                    </v-text-field>
                                </v-flex>
                                <v-flex>
                                    <v-text-field v-model="height" type="number" :rules="[validation.required(), validation.requiredPositive()]"
                                                  label="Container Height" clearable required>
                                    </v-text-field>
                                </v-flex>
                            </v-row>
                            <v-row justify="center">
                                <v-flex>
                                    <v-text-field v-model="regularity" type="number" label="Polygons Regularity"
                                                  :rules="[validation.required(), validation.betweenValues(0, 100)()]"
                                                  hint="Variability of an edge (5% of variability)"
                                                  persistent-hint clearable>
                                    </v-text-field>
                                </v-flex>
                            </v-row>
                            <v-row justify="center">
                                <v-flex>
                                    <v-radio-group label="Choose approach" v-model="approach" row>
                                        <v-radio color="teal lighten-2" label="Dense packing"
                                                 :value="1"></v-radio>
                                        <v-radio color="teal lighten-2" label="Gravity simulation"
                                                 :value="0"></v-radio>
                                    </v-radio-group>
                                </v-flex>
                            </v-row>
                        </v-form>
                    </v-container>
                </div>
                <div v-show="tab === 1">
                    <v-container>
                        <v-form ref="multiplePackingForm">
                            <v-row justify="center">
                                <v-btn color="teal lighten-2" class="mr-2" dark @click="addLayer">
                                    Add Layer
                                </v-btn>
                                <v-btn :disabled="layers.length === 1" :dark="layers.length !== 1"
                                       color="teal lighten-2"
                                       @click="deleteLayer">
                                    Remove Layer
                                </v-btn>
                            </v-row>
                            <v-row justify="center">
                                <v-flex>
                                    <v-text-field v-model="width" type="number" label="Container Width"
                                                  :rules="[validation.required(), validation.requiredPositive()]"
                                                  clearable required>
                                    </v-text-field>
                                </v-flex>
                            </v-row>
                            <v-layout v-for="(item, index) in layers" :key="index" column>
                                <v-row>
                                    <v-flex pr-3>
                                        <v-text-field v-model="layers[index].height" type="number"
                                                      :rules="[validation.required(), validation.requiredPositive()]"
                                                      label="Container Height" clearable required>
                                        </v-text-field>
                                    </v-flex>
                                    <v-flex>
                                        <v-text-field v-model="layers[index].regularity" type="number"
                                                      :rules="[validation.required(), validation.betweenValues(0, 100)()]"
                                                      label="Polygons Regularity"
                                                      hint="Variability of an edge (5% of variability)"
                                                      persistent-hint clearable>
                                        </v-text-field>
                                    </v-flex>
                                </v-row>
                                <v-row>
                                    <v-flex>
                                        <v-select
                                                v-model="layers[index].polygons" :items="polygons"
                                                :rules="[validation.required(), validation.atLeastOneSelected()]"
                                                item-text="label" item-color="teal lighten-2"
                                                color="teal lighten-2" chips return-object
                                                label="Layer Polygons" multiple>
                                        </v-select>
                                    </v-flex>
                                </v-row>
                            </v-layout>
                            <v-row justify="center">
                                <v-flex>
                                    <v-radio-group label="Choose approach"
                                                   v-model="approach" row>
                                        <v-radio color="teal lighten-2" label="Dense packing"
                                                 :value="1"></v-radio>
                                        <v-radio color="teal lighten-2" label="Gravity simulation"
                                                 :value="0"></v-radio>
                                    </v-radio-group>
                                </v-flex>
                            </v-row>
                        </v-form>
                    </v-container>
                </div>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="closeDialog">Close</v-btn>
                <v-btn v-if="tab === 0" dark color="teal lighten-2" @click.native="execute">Execute
                    Algorithm
                </v-btn>
                <v-btn v-else dark color="teal lighten-2" @click.native="executeMultiLayer">Execute
                    Algorithm
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import validation from './../../services/validation.service';

    export default {
        name: "DialogNewPacking",
        props: {
            dialog: {
                type: Boolean,
                defaultValue: false,
            },
        },
        data() {
            return {
                validation: validation,
                piledOption: ["Single Layer", "Multiple Layers"],
                tab: 0,
                layers: [{
                    height: 75,
                    regularity: 5
                }],
                width: 150,
                height: 75,
                randomShape: false,
                regularity: 5,
                approach: 1,
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
            clearForm(){
                this.tab = 0;
                this.layers = [{
                    height: 75,
                    regularity: 5,
                    polygons: []
                }];
                this.width =  150;
                this.height= 75;
                this.randomShape = false;
                this.regularity = 5;
                this.approach = 1;
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
                    this.$emit("execute-multi-layer",{
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