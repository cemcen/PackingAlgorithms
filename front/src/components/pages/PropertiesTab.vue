<template>
    <div>
        <properties-form ref="propertiesForm" :is-editing="isEditing"/>
        <v-list two-line>
            <v-row class="right-layout" align="end">
                <v-flex pt-3>
                    <v-btn dark block bottom right color="teal lighten-2" @click="openDialog">
                        New Property
                    </v-btn>
                </v-flex>
            </v-row>
            <div class="text-centered font-weight-light grey--text title mb-2" v-show="properties.length === 0">
                No properties registered.
            </div>
            <v-divider></v-divider>
            <template v-for="(key, value) in Object.entries(properties)">

                <v-list-item :key="key[1].label">

                    <!--<v-list-item-action class="checkbox-width">
                        <v-checkbox color="teal lighten-2" v-model="key[1].selected"></v-checkbox>
                    </v-list-item-action>-->

                    <v-list-item-avatar>
                        <swatches v-model="key[1].color" disabled colors="material-basic"/>
                    </v-list-item-avatar>

                    <v-list-item-content>
                        <v-list-item-title v-html="key[1].label"></v-list-item-title>
                        <v-list-item-subtitle>
                            <span>Type of value: {{ key[1].typeOfValue }}</span> <br/>
                            <span>Default value: {{ key[1].default? key[1].default : 'Not Defined' }}</span>
                        </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action class="checkbox-width">
                        <v-layout row>
                            <v-flex>
                                <v-btn icon @click="editItem(key[0])">
                                    <v-icon color="teal lighten-2">mdi-pencil</v-icon>
                                </v-btn>
                            </v-flex>
                            <v-flex>
                                <v-btn icon @click="deleteItem(key[0])">
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
    import Swatches from 'vue-swatches'

    // Import the styles too, globally
    import "vue-swatches/dist/vue-swatches.min.css"
    import PropertiesForm from "../templates/PropertiesForm.vue";

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
            }
        },
        computed: {
            properties () {
                return this.$store.getters.getProperties;
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
                this.$refs.propertiesForm.openDialog();
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
        margin-right: 10px;
        margin-bottom: 10px;
    }
</style>