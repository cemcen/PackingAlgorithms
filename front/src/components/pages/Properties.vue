<template>
    <div class="pa-3">
        <properties-form ref="propertiesForm" :is-editing="isEditing"/>
        <v-card>
            <v-toolbar color="grey lighten-3" light tabs class="elevation-1">
                <v-toolbar-title>Properties</v-toolbar-title>
                <v-spacer/>
                <v-btn dark color="teal lighten-2" @click="openDialog">
                    New Property
                </v-btn>
            </v-toolbar>
            <v-card-text class="pa-0">
                <v-data-table :headers="headers" :items="getProperties" class="elevation-0"
                              no-data-text=" No properties registered." item-key="label" hide-default-footer>
                    <template slot="item" slot-scope="props">
                        <tr>
                            <td>{{ props.item["label"] }}</td>
                            <td>
                                <v-chip :color="props.item['color']">
                                    <v-avatar >
                                        <v-icon color="transparent">mdi-palette</v-icon>
                                    </v-avatar>
                                </v-chip>
                            </td>
                            <td>{{ props.item["typeOfValue"] }}</td>
                            <td>{{ props.item["default"]? props.item["default"]: 'Not Defined'}}</td>
                            <td>
                                <v-tooltip top>
                                    <template v-slot:activator="{ on }">
                                        <v-btn icon @click="editItem(props.item['key'])">
                                            <v-icon color="teal lighten-2">mdi-pencil</v-icon>
                                        </v-btn>
                                    </template>
                                    <span>Edit Polygon</span>
                                </v-tooltip>
                                <v-tooltip top>
                                    <template v-slot:activator="{ on }">
                                        <v-btn icon @click="deleteItem(props.item['key'])">
                                            <v-icon color="red lighten-2">mdi-delete</v-icon>
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
    import {Validator} from 'vee-validate';
    import Swatches from 'vue-swatches'

    // Import the styles too, globally
    import "vue-swatches/dist/vue-swatches.min.css"
    import PropertiesForm from "../templates/forms/PropertiesForm.vue";

    Validator.extend('isBiggerThanZero', {
        getMessage: field => 'The ' + field + ' is not greater than 0.',
        validate: value => value > 0
    });

    export default {
        $_veeValidate: {
            validator: 'new'
        },
        components: {
            PropertiesForm,
            Swatches,
        },
        data() {
            return {
                isEditing: false,
                headers: [
                    {text: 'Label', value: "label"},
                    {text: 'Color', value: "color"},
                    {text: 'Type', value: "typeOfValue"},
                    {text: 'Default Value', value: "default"},
                    {text: 'Options', value: "options", sortable: false},
                ]
            }
        },
        computed: {
            properties () {
                return this.$store.getters.getProperties;
            },
            getProperties() {
                return Object.entries(this.properties).map(k => {
                    return {
                        'label': k[1].label,
                        'typeOfValue': k[1].typeOfValue,
                        'color': k[1].color,
                        'default': k[1].default,
                        'key': k[0],
                    }
                });
            }
        },
        methods: {
            editItem(item) {
                this.isEditing = true;
                this.$refs.propertiesForm.openDialog(this.properties[item]);
            },
            deleteItem(item) {
                if(confirm('Are you sure you want to delete this property?')) {
                    this.$store.commit('deleteProperty', item);
                }
            },
            openDialog() {
                this.isEditing = false;
                this.$refs.propertiesForm.openDialog();
            },
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
        margin-right: 10px;
        margin-bottom: 10px;
    }
</style>