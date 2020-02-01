<template>
    <v-dialog v-model="dialog" persistent max-width="500px">
        <v-card color="#ffffff">
            <v-card-title>
                <span class="headline">Assign Properties</span>
            </v-card-title>

            <v-card-text>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-select label="Choose which entities should have these properties"
                                  item-text="name"
                                  v-model="selectedOptionProperties" :items="optionProperties"
                                  return-object>
                        </v-select>
                    </v-col>
                </v-row>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-select label="From the selected entities choose where to assign"
                                  item-text="name"
                                  v-model="selectedOptionType" :items="optionType" return-object>
                        </v-select>
                    </v-col>
                </v-row>
                <v-row justify="center">
                    <v-list style="width: 100%" two-line>
                        <div class="text-centered font-weight-light grey--text title mb-2"
                             v-show="properties.length === 0">
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
                <v-btn color="teal lighten-2" text @click.native="closeDialog()">Close</v-btn>
                <v-btn dark color="teal lighten-2" @click.native="assignProperties">Assign Properties</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import Swatches from 'vue-swatches';
    import "vue-swatches/dist/vue-swatches.min.css"
    export default {
        name: "AssignProperties",
        props: {
            dialog: {
                type: Boolean,
                defaultValue: false,
            },
        },
        components: {
            Swatches,
        },
        data() {
            return {
                optionProperties: [
                    {
                        value: 0,
                        name: "Selected Polygons and Holes"
                    },
                    {
                        value: 1,
                        name: "All Polygons and Holes"
                    }
                ],
                optionType: [
                    {
                        value: 0,
                        name: "Polygons and Holes"
                    },
                    {
                        value: 1,
                        name: "Polygons Only"
                    },
                    {
                        value: 2,
                        name: "Holes Only"
                    }
                ],
                selectedOptionProperties: {
                    value: 0,
                    name: "Selected Polygons and Holes"
                },
                selectedOptionType: {
                    value: 0,
                    name: "Polygons and Holes"
                },
            }
        },
        methods: {
            closeDialog() {
                this.resetSelectedProperties();
                this.$emit('closeDialog', false);
            },
            assignProperties() {
                this.$emit('assignProperties', this.selectedOptionProperties, this.selectedOptionType);
                this.resetSelectedProperties();
            },
            resetSelectedProperties() {
                let nProperties = this.properties;
                Object.keys(nProperties).forEach(function (item) {
                    nProperties[item].selected = false;
                });
                this.$store.commit("editProperties", nProperties);
            }
        },
        computed: {
            properties() {
                return this.$store.getters.getProperties;
            },
        }
    }
</script>

<style scoped>

</style>