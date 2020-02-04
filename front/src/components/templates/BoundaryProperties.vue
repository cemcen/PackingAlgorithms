<template>
    <v-card color="#ffffff" class="ma-3 fill-height" style="max-height: 80%">
        <v-card-title>
            <span class="headline">Assign Properties</span>
            <v-spacer></v-spacer>
            <v-btn dark color="primary" @click.native="openDialog()" >
                <v-icon class="mr-1">mdi-plus</v-icon>
                New Property
            </v-btn>
        </v-card-title>
        <properties-form ref="propertiesForm" :is-editing="isEditing"/>

        <v-card-text style="max-height: 80%; overflow: auto;">
            <v-row justify="center">
                <v-col class="pa-0 pr-3 pl-3">
                    <v-select label="All border elements or only selected ones."
                              v-model="selectedOptionProperties" item-text="name"
                              :items="optionProperties" return-object>
                    </v-select>
                </v-col>
            </v-row>
            <v-row justify="center">
                <v-col class="pa-0 pr-3 pl-3">
                    <v-select label="All border elements, only segments or only vertices." item-text="name"
                              v-model="selectedOptionType" :items="optionType" return-object>
                    </v-select>
                </v-col>
            </v-row>
            <v-row justify="center">
                <v-list style="width: 100%; display: contents;" two-line>
                    <div class="text-centered font-weight-light grey--text title mb-2"
                         v-show="Object.keys(properties).length === 0">
                        No properties registered.
                    </div>
                    <template v-for="(key, value) in Object.entries(properties)">

                        <v-list-item :key="key[1].label">

                            <v-list-item-action>
                                <v-checkbox color="teal lighten-2"
                                            v-model="key[1].selected"></v-checkbox>
                            </v-list-item-action>

                            <v-list-item-avatar>
                                <swatches v-model="key[1].color" disabled class="mr-3"
                                          colors="material-basic"/>
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title v-html="key[1].label"></v-list-item-title>
                                <v-list-item-subtitle>
                                    <span>Type of value: {{ key[1].typeOfValue }}</span> <br/>
                                    <span>Default value: {{ key[1].default? key[1].default : 'Not Defined' }}</span>
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                        <v-divider></v-divider>
                    </template>
                </v-list>
            </v-row>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn dark color="teal lighten-2" @click.native="assignProperties">
                <v-icon class="mr-1">mdi-palette</v-icon>
                Assign Properties
            </v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import Swatches from 'vue-swatches';
    import "vue-swatches/dist/vue-swatches.min.css"
    import PropertiesForm from "./PropertiesForm.vue";
    export default {
        name: "BorderProperties",
        props: {},
        components: {
            Swatches,
            PropertiesForm
        },
        data() {
            return {
                isEditing: false,
                selectedOptionProperties: {name: "Entire Boundary", value: 0},
                selectedOptionType: {name: "All Entities", value: 0 },
                optionProperties: [
                    {name: "Entire Boundary", value: 0 },
                    {name: "Selected Entities Only", value: 1 },
                ],
                optionType: [
                    {name: "All Entities", value: 0 },
                    {name: "Nodes Only", value: 1 },
                    {name: "Segments Only", value: 2 },
                ],
            }
        },
        computed: {
            properties () {
                return this.$store.getters.getProperties;
            }
        },
        methods: {
            openDialog() {
                this.$refs.propertiesForm.openDialog();
            },
            assignProperties() {
                this.$emit("assign-properties", this.selectedOptionProperties, this.selectedOptionType);
            },
        }
    }
</script>

<style scoped>
</style>