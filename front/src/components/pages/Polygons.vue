<template>
    <div class="pa-3">
        <v-dialog v-model="dialog" eager persistent max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">{{ formTitle }}</span>
                </v-card-title>

                <v-card-text>
                    <v-form ref="polygonForm">
                        <v-row justify="center">
                            <v-flex sm12>
                                <v-text-field v-model="editedItem.label" :rules="[validation.required()]"
                                              label="Label" clearable required>
                                </v-text-field>
                            </v-flex>
                            <v-flex sm12>
                                <v-text-field v-model="editedItem.numberOfVertex"
                                              :rules="[validation.required(), validation.requiredPositive()]"
                                              type="number" label="Number of vertices"
                                              clearable required>
                                </v-text-field>
                            </v-flex>

                            <v-flex sm12>
                                <v-text-field v-model="editedItem.radius"
                                              :rules="[validation.required(), validation.requiredPositive()]"
                                              type="number" label="Radius" clearable
                                              required>
                                </v-text-field>
                            </v-flex>
                            <v-flex sm12>
                                <v-text-field v-model="editedItem.percentage" type="number"
                                              :rules="[validation.required(), validation.requiredPositive()]"
                                              label="Probability of appearance" clearable required>
                                </v-text-field>
                            </v-flex>
                        </v-row>
                    </v-form>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                    <v-btn dark color="teal lighten-2" @keyup.enter="save" @click.native="save">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-card>
            <v-toolbar color="grey lighten-3" light tabs class="elevation-1">
                <v-toolbar-title>Polygons</v-toolbar-title>
                <v-spacer/>
                <v-btn dark color="teal lighten-2" @click="openDialog">
                    <v-icon class="mr-1">{{icons['mdi-shape-polygon-plus']}}</v-icon>
                    New Polygon
                </v-btn>
            </v-toolbar>
            <v-card-text class="pa-0">
                <v-data-table :headers="headers" :items="polygons" class="elevation-0"
                              no-data-text="No polygons registered." item-key="label" hide-default-footer>
                    <template slot="item" slot-scope="props">
                        <tr>
                            <td>{{ props.item["label"] }}</td>
                            <td>{{ props.item["numberOfVertex"] }}</td>
                            <td>{{ props.item["percentage"] }}%</td>
                            <td>{{ props.item["radius"] }}</td>
                            <td>
                                <v-tooltip top>
                                    <template v-slot:activator="{ on }">
                                        <v-btn icon @click="editItem(props.item)">
                                            <v-icon color="teal lighten-2">{{icons['mdi-pencil']}}</v-icon>
                                        </v-btn>
                                    </template>
                                    <span>Edit Polygon</span>
                                </v-tooltip>
                                <v-tooltip top>
                                    <template v-slot:activator="{ on }">
                                        <v-btn icon @click="deleteItem(props.item)">
                                            <v-icon color="red lighten-2">{{icons['mdi-delete']}}</v-icon>
                                        </v-btn>
                                    </template>
                                    <span>Delete Polygon</span>
                                </v-tooltip>
                            </td>
                        </tr>
                    </template>
                </v-data-table>
            </v-card-text>
        </v-card>
    </div>
</template>

<script>
    import validation from './../../services/validation.service';
    import {mdiShapePolygonPlus, mdiPencil, mdiDelete} from '@mdi/js';

    export default {
        data() {
            return {
                icons: {
                    "mdi-shape-polygon-plus": mdiShapePolygonPlus,
                    "mdi-pencil": mdiPencil,
                    "mdi-delete": mdiDelete,
                },
                validation: validation,
                dialog: false,
                isEditing: false,
                editedIndex: -1,
                selectedTab: 0,
                headers: [
                    {text: 'Label', value: "label"},
                    {text: 'Number of Vertices', value: "numberOfVertex"},
                    {text: 'Percentage', value: "percentage"},
                    {text: 'Radius', value: "radius"},
                    {text: 'Options', value: "options", sortable: false},
                ],
                editedItem: {
                    label: null,
                    numberOfVertex: null,
                    radius: null,
                    percentage: null
                },
            }
        },
        created() {
            this.validation.changeLanguage('en');
        },
        computed: {
            formTitle() {
                return this.editedIndex === -1 ? 'Add Polygon' : 'Edit Polygon'
            },
            polygons () {
                return this.$store.getters.getPolygons;
            }
        },
        methods: {
            deleteItem(item) {
                if(confirm('Are you sure you want to delete this polygon?')) {
                    this.$store.commit('deletePolygon', item);
                }
            },
            editItem(item) {
                this.editedIndex = this.polygons.indexOf(item);
                this.editedItem = Object.assign({}, item);
                this.isEditing = true;
                this.dialog = true;
            },
            close() {
                this.dialog = false;
                this.isEditing = false;
                this.resetValidation();
            },
            save() {
                if (this.$refs.polygonForm.validate()) {
                    if (this.isEditing) {
                        this.$store.commit('editPolygon', { index: this.editedIndex, item: this.editedItem});
                    } else {
                        this.$store.commit('addPolygon', this.editedItem);
                    }
                    this.close();
                }
            },
            resetValidation() {
                this.$refs.polygonForm.resetValidation();
            },
            openDialog() {
                this.resetValidation();
                this.dialog = true;
                this.editedItem = {
                    label: null,
                    numberOfVertex: null,
                    radius: null,
                    percentage: null
                };
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