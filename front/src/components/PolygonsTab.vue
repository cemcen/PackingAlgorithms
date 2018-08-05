<template>
    <div>
        <v-toolbar dark>
            <v-toolbar-title>List of Polygons</v-toolbar-title>
            <v-divider
                    class="mx-2"
                    inset
                    vertical
            ></v-divider>
            <v-spacer></v-spacer>
            <v-dialog v-model="dialog" max-width="500px">
                <v-btn slot="activator" color="teal darken-1" dark class="mb-2">New Item</v-btn>
                <v-card>
                    <v-card-title>
                        <span class="headline">{{ formTitle }}</span>
                    </v-card-title>

                    <v-card-text>
                        <v-layout justify-center>
                            <v-flex>
                                <v-text-field v-validate="'required'"
                                              :error-messages="errors.collect('label')"
                                              v-model="editedItem.label"
                                              label="Label"
                                              data-vv-name="label"
                                              clearable
                                              required>
                                </v-text-field>
                                <v-text-field v-validate="'required|min_value:3'"
                                              :error-messages="errors.collect('vertex')"
                                              v-model="editedItem.numberOfVertex"
                                              type="number"
                                              label="Number of vertex"
                                              data-vv-name="vertex"
                                              clearable
                                              required>
                                </v-text-field>
                                <v-text-field v-validate="'required|isBiggerThanZero'"
                                              :error-messages="errors.collect('radius')"
                                              v-model="editedItem.radius"
                                              type="number"
                                              label="Radius"
                                              data-vv-name="radius"
                                              clearable
                                              required>
                                </v-text-field>
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
                        </v-layout>
                    </v-card-text>

                    <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn color="blue darken-1" flat @click.native="close">Cancel</v-btn>
                        <v-btn color="blue darken-1" flat @click.native="save">Save</v-btn>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </v-toolbar>
        <v-data-table
                :headers="headers"
                :items="polygons"
                hide-actions
                class="elevation-1"
        >
            <template slot="items" slot-scope="props">
                <td>{{ props.item.label }}</td>
                <td class="text-xs-right">{{ props.item.numberOfVertex }}</td>
                <td class="text-xs-right">{{ props.item.radius }}</td>
                <td class="text-xs-right">{{ props.item.percentage }}</td>
                <td class="justify-center layout px-0">
                    <v-icon small class="mr-2" @click="editItem(props.item)">
                        edit
                    </v-icon>
                    <v-icon small @click="deleteItem(props.item)">
                        delete
                    </v-icon>
                </td>
            </template>
            <template slot="no-data">
                <v-alert :value="true" color="teal darken-1" icon="warning">
                    No polygons for packing algorithm.
                </v-alert>
            </template>
        </v-data-table>
    </div>
</template>

<script>
    import { Validator } from 'vee-validate';
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
            }
        },
        dictionary: {
            custom: {
                label: {
                    required: () => 'Must enter a label.',
                },
                vertex: {
                    required: 'Must enter the number of vertex.',
                    min: 'The polygon must have at least 3 vertex'
                },
                percentage: {
                    required: 'Must enter the probability of appearance.'
                },
                radius: {
                    required: 'Must enter the radius.'
                }
            }
        },
        computed: {
            formTitle() {
                return this.editedIndex === -1 ? 'Add Polygon' : 'Edit Polygon'
            }
        },
        mounted() {
            if (localStorage.getItem('polygons')) this.polygons = JSON.parse(localStorage.getItem('polygons'));
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
                confirm('Are you sure you want to delete this item?') && this.polygons.splice(index, 1);
            },
            close() {
                this.dialog = false;
                setTimeout(() => {
                    this.editedItem = Object.assign({}, this.defaultItem);
                    this.editedIndex = -1;
                }, 300)
            },
            save() {
                this.$validator.validateAll().then(result =>{
                    if(result) {
                        if (this.editedIndex > -1) {
                            Object.assign(this.polygons[this.editedIndex], this.editedItem);
                        } else {
                            this.polygons.push(this.editedItem);
                        }
                        this.close();
                    }
                });
            }
        }
    }
</script>

<style scoped lang="scss">

    .page-container {
        overflow: auto;
        margin: 2px;
    }
</style>