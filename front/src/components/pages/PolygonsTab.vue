<template>
    <div>
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
                                              type="number" label="Number of vertex"
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
    import validation from './../../services/validation.service';

    export default {
        data() {
            return {
                validation: validation,
                dialog: false,
                isEditing: false,
                editedIndex: -1,
                selectedTab: 0,
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