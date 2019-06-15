<template>
    <div>
        <v-dialog v-model="dialog" persistent max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">{{ formTitle }}</span>
                </v-card-title>

                <v-card-text>
                    <v-layout justify-center column>
                        <v-flex>
                            <v-text-field v-validate="'required'" :error-messages="errors.collect('label')"
                                          v-model="editedItem.label" label="Label" data-vv-name="label"
                                          clearable required>
                            </v-text-field>
                        </v-flex>
                        <v-flex>
                            <v-select v-validate="'required'" :error-messages="errors.collect('typeOfValue')"
                                      v-model="editedItem.typeOfValue" :items="typeOfValues"
                                      label="Type of Value"  data-vv-name="typeOfValue"
                                      clearable required></v-select>
                        </v-flex>
                    </v-layout>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="black" flat @click.native="close">Cancel</v-btn>
                    <v-btn color="teal lighten-2" @keyup.enter="save" @click.native="save">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-list two-line>
            <v-btn fixed fab bottom right color="teal lighten-2" @click="openDialog">
                <v-icon>add</v-icon>
            </v-btn>
            <template v-for="(item, index) in properties">

                <v-list-tile :key="item.label" avatar @click="">

                    <v-list-tile-content>
                        <v-list-tile-title v-html="item.label"></v-list-tile-title>
                        <v-list-tile-sub-title>
                            <span>Type of value: {{ item.typeOfValue }}</span>
                        </v-list-tile-sub-title>
                    </v-list-tile-content>
                    <v-list-tile-action>
                        <v-layout row>
                            <v-flex>
                                <v-btn icon ripple @click="editItem(item)">
                                    <v-icon color="teal lighten-2">edit</v-icon>
                                </v-btn>
                            </v-flex>
                            <v-flex>
                                <v-btn icon ripple @click="deleteItem(item)">
                                    <v-icon color="red lighten-2">delete</v-icon>
                                </v-btn>
                            </v-flex>
                        </v-layout>
                    </v-list-tile-action>
                </v-list-tile>
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
                isEditing: false,
                properties: [],
                editedItem: {
                    label: '',
                    typeOfValue: ''
                },
                typeOfValues: ['Number', 'String'],
                dictionary: {
                    custom: {
                        label: {
                            required: 'Must enter a label.',
                        },
                        typeOfValue: {
                            required: 'Must enter the type of this property.',
                        },
                    }
                },
            }
        },
        computed: {
            formTitle() {
                return (!this.isEditing)? 'Add Property' : 'Edit Property';
            }
        },
        mounted() {
            if (localStorage.getItem('properties')) this.properties = JSON.parse(localStorage.getItem('properties'));
            this.$validator.localize('es', this.dictionary);
        },
        watch: {
            properties: {
                handler() {
                    localStorage.setItem('properties', JSON.stringify(this.properties));
                },
                deep: true
            },
            dialog(val) {
                val || this.close()
            }
        },
        methods: {
            editItem(item) {

            },
            deleteItem(item) {

            },
            close() {
                this.dialog = false;
            },
            save(){
                this.$validator.validateAll().then(result => {
                    if (result) {
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
</style>