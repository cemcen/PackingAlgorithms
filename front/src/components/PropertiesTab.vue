<template>
    <div>
        <v-dialog v-model="dialog" persistent max-width="500px">
            <v-card>
                <v-card-title>
                    <span class="headline">{{ formTitle }}</span>
                </v-card-title>

                <v-card-text>
                    <v-layout justify-center>
                        <v-flex>
                            <v-text-field v-validate="'required'" :error-messages="errors.collect('label')"
                                          v-model="editedItem.label" label="Label" data-vv-name="label"
                                          clearable required>
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                    <v-layout>
                        <v-flex>
                            <swatches v-model="editedItem.color" colors="material-basic" inline/>
                        </v-flex>
                    </v-layout>
                    <v-layout>
                        <v-flex>
                            <v-select v-validate="'required'" :error-messages="errors.collect('typeOfValue')"
                                      v-model="editedItem.typeOfValue" :items="typeOfValues"
                                      label="Type of Value"  data-vv-name="typeOfValue"
                                      clearable required></v-select>
                        </v-flex>
                    </v-layout>
                    <v-layout>
                        <v-flex>
                            <v-text-field v-model="editedItem.default" label="Default Value"
                                          :type="editedItem.typeOfValue === 'Number'? 'number': 'text'"
                                          clearable required>
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="teal lighten-2" flat @click.native="close">Cancel</v-btn>
                    <v-btn dark color="teal lighten-2" @keyup.enter="save" @click.native="save">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-list two-line>
            <v-btn fixed dark fab bottom right color="teal lighten-2" @click="openDialog">
                <v-icon>add</v-icon>
            </v-btn>
            <v-layout class="right-layout" align-end>
                <v-flex>
                    <v-btn :dark="Object.entries(properties).some(prop => prop[1].selected)" depressed
                           :disabled="!Object.entries(properties).some(prop => prop[1].selected)" color="teal lighten-2"
                           @click="assignProperties">
                        Assign Properties
                    </v-btn>
                </v-flex>
            </v-layout>
            <div class="text-centered font-weight-light grey--text title mb-2" v-show="properties.length === 0">
                No properties registered.
            </div>
            <template v-for="(key, value) in Object.entries(properties)">

                <v-list-tile :key="key[1].label" avatar @click="">

                    <v-list-tile-action class="checkbox-width">
                        <v-checkbox color="teal lighten-2" v-model="key[1].selected"></v-checkbox>
                    </v-list-tile-action>

                    <v-list-tile-avatar>
                        <swatches v-model="key[1].color" disabled colors="material-basic"/>
                    </v-list-tile-avatar>

                    <v-list-tile-content>
                        <v-list-tile-title v-html="key[1].label"></v-list-tile-title>
                        <v-list-tile-sub-title>
                            <span>Type of value: {{ key[1].typeOfValue }}</span> <br/>
                            <span>Default value: {{ key[1].default? key[1].default : 'Not Defined' }}</span>
                        </v-list-tile-sub-title>
                    </v-list-tile-content>
                    <v-list-tile-action class="checkbox-width">
                        <v-layout column>
                            <v-flex>
                                <v-btn icon ripple @click="editItem(key[0])">
                                    <v-icon color="teal lighten-2">edit</v-icon>
                                </v-btn>
                            </v-flex>
                            <v-flex>
                                <v-btn icon ripple @click="deleteItem(key[0])">
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
    import Swatches from 'vue-swatches'

    // Import the styles too, globally
    import "vue-swatches/dist/vue-swatches.min.css"

    Validator.extend('isBiggerThanZero', {
        getMessage: field => 'The ' + field + ' is not greater than 0.',
        validate: value => value > 0
    });

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        components: {
            Swatches,
        },
        data() {
            return {
                dialog: false,
                isEditing: false,
                properties: {},
                editedItem: {
                    label: '',
                    typeOfValue: '',
                    color: "#F44336"
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
            if (localStorage.getItem('properties')) {
                this.properties = JSON.parse(localStorage.getItem('properties'));

                let mProperties = this.properties;
                Object.keys(this.properties).forEach(function(key) {
                    mProperties[key].selected = false;
                });
            }
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
                this.editedItem = Object.assign({}, this.properties[item]);
                this.isEditing = true;
                this.dialog = true;
            },
            deleteItem(item) {
                if(confirm('Are you sure you want to delete this property?')) {
                    delete this.properties[item];
                }
            },
            close() {
                this.dialog = false;
            },
            save(){
                this.$validator.validateAll().then(result => {
                    if (result) {
                        if(this.isEditing) {
                            Object.assign(this.properties[this.editedItem.label], this.editedItem);
                            this.isEditing = false;
                        } else {
                            this.properties[this.editedItem.label] = this.editedItem;
                            this.properties[this.editedItem.label].selected = false;
                        }
                        localStorage.setItem('properties', JSON.stringify(this.properties));
                        this.close();
                    }
                });
            },
            assignProperties() {
                this.$emit('assign', true)
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

    .text-centered{
        text-align: center;
    }

    .checkbox-width{
        min-width: 20px;
    }

    .right-layout{
        display: grid;
        justify-content: flex-end;
    }
</style>