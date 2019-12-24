<template>
    <div>
        <v-dialog v-model="dialog" persistent max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">{{ formTitle }}</span>
                </v-card-title>

                <v-card-text>
                    <v-row justify="center">
                        <v-flex sm12>
                            <v-text-field v-validate="'required'"
                                          :error-messages="errors.collect('label')"
                                          v-model="editedItem.label"
                                          label="Label"
                                          data-vv-name="label"
                                          clearable
                                          required>
                            </v-text-field>
                        </v-flex>
                        <v-flex sm12>
                            <v-text-field v-validate="'required|min_value:3'"
                                          :error-messages="errors.collect('vertex')"
                                          v-model="editedItem.numberOfVertex"
                                          type="number"
                                          label="Number of vertex"
                                          data-vv-name="vertex"
                                          clearable
                                          required>
                            </v-text-field>
                        </v-flex>

                        <v-flex sm12>
                            <v-text-field v-validate="'required|isBiggerThanZero'"
                                          :error-messages="errors.collect('radius')"
                                          v-model="editedItem.radius"
                                          type="number"
                                          label="Radius"
                                          data-vv-name="radius"
                                          clearable
                                          required>
                            </v-text-field>
                        </v-flex>
                        <v-flex sm12>
                            <v-text-field v-validate="'required|isBiggerThanZero'"
                                          :error-messages="errors.collect('percentage')"
                                          v-model="editedItem.percentage"
                                          type="number"
                                          label="Probability of appearance"
                                          data-vv-name="percentage"
                                          clearable
                                          required>
                            </v-text-field>
                        </v-flex>
                    </v-row>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                    <v-btn dark color="teal lighten-2" @keyup.enter="save" @click.native="save">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-list two-line>
            <v-row class="right-layout" align="end">
                <v-flex pt-3>
                    <v-btn dark block bottom right color="teal lighten-2" @click="openDialog">
                        New Polygon
                    </v-btn>
                </v-flex>
            </v-row>
            <div class="text-centered font-weight-light grey--text title mb-2" v-show="polygons.length === 0">
                No polygons registered.
            </div>
            <v-divider></v-divider>
            <template v-for="(item, index) in polygons">

                <v-list-item :key="item.label">

                    <v-list-item-content>
                        <v-list-item-title v-html="item.label"></v-list-item-title>
                        <v-list-item-subtitle>
                            <span>Number of Vertex: {{ item.numberOfVertex }}</span> <br/>
                            <span>Percentage: {{ item.percentage }}%,</span>
                            <span>Radius: {{ item.radius }}</span>
                        </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action>
                        <v-layout row>
                            <v-flex>
                                <v-btn icon @click="editItem(item)">
                                    <v-icon color="teal lighten-2">mdi-pencil</v-icon>
                                </v-btn>
                            </v-flex>
                            <v-flex>
                                <v-btn icon @click="deleteItem(item)">
                                    <v-icon color="red lighten-2">mdi-delete</v-icon>
                                </v-btn>
                            </v-flex>
                        </v-layout>
                    </v-list-item-action>
                </v-list-item>
                <v-divider></v-divider>
            </template>
        </v-list>
    </div>
</template>

<script>
    import {Validator} from 'vee-validate';

    Validator.extend('isBiggerThanZero', {
        getMessage: field => 'The ' + field + ' is not greater than 0.',
        validate: value => value > 0
    });

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        data() {
            return {
                dialog: false,
                editedIndex: -1,
                selectedTab: 0,
                script: null,
                numberOfVertex: 4,
                radius: 40,
                polygons: [],
                ps: null,
                editedItem: {
                    label: '',
                    numberOfVertex: 0,
                    radius: 0,
                    percentage: 0
                },
                defaultItem: {
                    label: '',
                    numberOfVertex: 0,
                    radius: 0,
                    percentage: 0
                },
                polygon: {
                    label: "",
                    numberOfVertex: 4,
                    radius: 40,
                    percentage: null
                },
                headers: [
                    {text: 'Label', value: 'label'},
                    {text: 'Number of Vertex', value: 'numberOfVertex'},
                    {text: 'Radius', value: 'radius'},
                    {text: 'Probability', value: 'percentage'},
                    {text: 'Actions', value: 'name', sortable: false}
                ],
                dictionary: {
                    custom: {
                        label: {
                            required: 'Must enter a label.',
                        },
                        vertex: {
                            required: 'Must enter the number of vertex.',
                            min_value: 'The polygon must have at least 3 vertex'
                        },
                        percentage: {
                            required: 'Must enter the probability of appearance.'
                        },
                        radius: {
                            required: 'Must enter the radius.'
                        }
                    }
                },
            }
        },
        computed: {
            formTitle() {
                return this.editedIndex === -1 ? 'Add Polygon' : 'Edit Polygon'
            }
        },
        mounted() {
            if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
            this.$validator.localize('es', this.dictionary);
        },
        watch: {
            polygons: {
                handler() {
                    localStorage.setItem('polygons', JSON.stringify(this.polygons));
                },
                deep: true
            },
            dialog(val) {
                val || this.close()
            }
        },
        methods: {
            editItem(item) {
                this.editedIndex = this.polygons.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.dialog = true;
            },
            deleteItem(item) {
                const index = this.polygons.indexOf(item);
                confirm('Are you sure you want to delete this polygon?') && this.polygons.splice(index, 1);
            },
            close() {
                this.dialog = false;
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedIndex = -1;
                }, 300)
            },
            save() {
                this.$validator.validateAll().then(result => {
                    if (result) {
                        if (this.editedIndex !== -1) {
                            Object.assign(this.polygons[this.editedIndex], this.editedItem);
                        } else {
                            this.polygons.push(this.editedItem);
                        }
                        localStorage.setItem('polygons', JSON.stringify(this.properties));
                        this.close();
                    }
                });
            },
            openDialog() {
                this.dialog = true;
                this.errors.clear();
            }
        }
    }
</script>

<style scoped lang="scss">

    .page-container {
        overflow: auto;
        margin: 2px;
    }

    .text-centered {
        text-align: center;
    }

    .right-layout {
        display: grid;
        justify-content: flex-end;
        margin-right: 10px;
        margin-bottom: 10px;
    }
</style>